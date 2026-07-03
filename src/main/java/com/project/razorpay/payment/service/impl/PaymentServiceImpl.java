package com.project.razorpay.payment.service.impl;

import com.project.razorpay.common.enums.OrderStatus;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentGatewayRouter paymentGatewayRouter;

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

        PaymentResult result = paymentGatewayRouter.initiate(paymentRequest);

        switch (result) {
            case PaymentResult.Pending pending -> payment.setProcessorReference(pending.registrationRef());
            case PaymentResult.Failure failure -> {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
        }

        payment = paymentRepository.save(payment);
        order = orderRepository.save(order);

        return paymentMapper.toResponse(payment);
    }
}
