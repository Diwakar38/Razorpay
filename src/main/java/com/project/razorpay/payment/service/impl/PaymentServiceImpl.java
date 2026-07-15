package com.project.razorpay.payment.service.impl;

import com.project.razorpay.common.enums.OrderStatus;
import com.project.razorpay.common.enums.PaymentEvent;
import com.project.razorpay.common.enums.PaymentStatus;
import com.project.razorpay.common.exceptions.BusinessRuleViolationException;
import com.project.razorpay.common.exceptions.ResourceNotFoundException;
import com.project.razorpay.payment.dto.request.PaymentInitRequest;
import com.project.razorpay.payment.dto.response.PaymentResponse;
import com.project.razorpay.payment.entity.OrderRecord;
import com.project.razorpay.payment.entity.Payment;
import com.project.razorpay.payment.gateway.PaymentGatewayRouter;
import com.project.razorpay.payment.gateway.dto.request.PaymentRequest;
import com.project.razorpay.payment.gateway.dto.response.PaymentResult;
import com.project.razorpay.payment.mapper.PaymentMapper;
import com.project.razorpay.payment.repository.OrderRepository;
import com.project.razorpay.payment.repository.PaymentRepository;
import com.project.razorpay.payment.service.PaymentService;
import com.project.razorpay.payment.statemachine.PaymentTransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentTransitionService paymentTransitionService;

    @Override
    @Transactional
    public PaymentResponse initiate(UUID merchantId, PaymentInitRequest request) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(request.orderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order",request.orderId()));

        if(order.getOrderStatus() != OrderStatus.CREATED && order.getOrderStatus() != OrderStatus.ATTEMPTED) {
            throw new BusinessRuleViolationException("ORDER_NOT_PAYABLE",
                                                     "Order cannot accept in status: " + order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.ATTEMPTED);
        order.setAttempts(order.getAttempts() + 1);
        Payment payment = Payment.builder()
                .order(order)
                .merchantId(merchantId)
                .amount(order.getAmount())
                .paymentMethod(request.method())
                .idempotencyKey(UUID.randomUUID().toString())
                .status(PaymentStatus.CREATED)
                .paymentMethod(request.method())
                .methodDetails(request.methodDetails())
                .build();

        payment = paymentRepository.save(payment);

        PaymentRequest paymentRequest = new PaymentRequest(
                payment.getId(),
                request.orderId(),
                merchantId,
                order.getAmount(),
                request.method(),
                request.methodDetails());

        paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_ATTEMPT);
        PaymentResult result = paymentGatewayRouter.initiate(paymentRequest);

        switch (result) {
            case PaymentResult.Pending pending -> payment.setProcessorReference(pending.registrationRef());
            case PaymentResult.Failure failure -> {
//                payment.setStatus(PaymentStatus.FAILED);
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            case PaymentResult.Success success -> {

            }
        }

        payment = paymentRepository.save(payment);
        order = orderRepository.save(order);

        // TODO: Send an outbox (kafka event)

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse capture(UUID merchantId, UUID paymentId) {
        Payment payment = paymentRepository.findByIdAndMerchantId(paymentId,merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_REQUEST);

        PaymentResult paymentResult = paymentGatewayRouter.capture(payment.getPaymentMethod(), paymentId);

        switch (paymentResult) {
            case PaymentResult.Success success -> {
                paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(LocalDateTime.now());
                log.info("Payment Captured, paymentId: {}", paymentId);
            }
            case PaymentResult.Failure failure -> {
                paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_FAILURE);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
                log.warn("Payment Failed, paymentId: {}", paymentId);
            }
            case PaymentResult.Pending pending -> {
            }
        }

        payment = paymentRepository.save(payment);

        // TODO: Send an outbox (kafka event)
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public void reslveAuthorization(UUID paymentId, boolean approve,
                                    String bankRef, String errorCode, String errorDescription) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        if(payment.getStatus() != PaymentStatus.AUTHORIZING) {
            log.warn("Payment is not in authorizing state, paymentId: {}, status: {}", paymentId, payment.getStatus());
            return;
        }

        OrderRecord orderRecord = payment.getOrder();

        if(approve) {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankRef);
            payment.setAuthorizedAt(LocalDateTime.now());
            // Auto Capture
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
            PaymentResult paymentResult = paymentGatewayRouter.capture(payment.getPaymentMethod(), paymentId);

            switch (paymentResult) {
                case PaymentResult.Success success -> {
                    paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
                    payment.setCapturedAt(LocalDateTime.now());
                    orderRecord.setOrderStatus(OrderStatus.PAID);
                }
                case PaymentResult.Pending pending -> {
                    paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
                }
                case PaymentResult.Failure failure -> {
                    paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
                    payment.setErrorCode(failure.errorCode());
                    payment.setErrorDescription(failure.errorDescription());
                }
            }
        } else {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
        }

        paymentRepository.save(payment);
        orderRepository.save(orderRecord);
        // TODO: Send an outbox (kafka event)
    }
}
