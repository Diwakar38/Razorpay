package com.project.razorpay.payment.gateway.adapter;

import com.project.razorpay.common.enums.PaymentMethod;
import com.project.razorpay.payment.gateway.PaymentAdapter;
import com.project.razorpay.payment.gateway.dto.request.PaymentRequest;
import com.project.razorpay.payment.gateway.dto.response.PaymentResult;
import com.project.razorpay.payment.processor.PaymentProcessorRouter;
import com.project.razorpay.payment.processor.dto.PaymentProcessRequest;
import com.project.razorpay.payment.processor.dto.PaymentProcessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("UPI")
@Slf4j
@RequiredArgsConstructor
public class UPIAdapter implements PaymentAdapter {

    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    public PaymentResult initiate(PaymentRequest request) {
        log.info("Initiate payment with UPI, paymentId: {}", request.paymentId());
        try {
            PaymentProcessRequest paymentProcessRequest = PaymentProcessRequest.nonCard(
                    request.paymentId(),
                    PaymentMethod.UPI,
                    request.amount(),
                    request.methodDetails()
            );

            PaymentProcessResponse response = paymentProcessorRouter.charge(paymentProcessRequest);

            return switch (response) {
                case PaymentProcessResponse.Failure failure ->
                        new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
                case PaymentProcessResponse.Pending pending -> new PaymentResult.Pending(pending.processorReference());
                case PaymentProcessResponse.Success success -> new PaymentResult.Success(success.bankReference());

            };
        } catch (Exception e) {
            log.warn("UPI failed, paymentId: {}", request.paymentId());
            return new PaymentResult.Failure("UPI_FAILED", e.getMessage());
        }
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("UPI_REF");
    }
}
