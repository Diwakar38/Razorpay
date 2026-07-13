package com.project.razorpay.payment.gateway.adapter;

import com.project.razorpay.payment.gateway.PaymentAdapter;
import com.project.razorpay.payment.gateway.dto.request.PaymentRequest;
import com.project.razorpay.payment.gateway.dto.response.PaymentResult;
import com.project.razorpay.payment.processor.dto.PaymentProcessResponse;
import com.project.razorpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentAdapter {

    private final VaultService vaultService;

    @Override
    public PaymentResult initiate(PaymentRequest request) {
        String token = request.methodDetails().get("token").toString();
        PaymentProcessResponse response = vaultService.charge(
                request.paymentId(), token, request.amount(), request.methodDetails()
        );
        return switch (response) {
            case PaymentProcessResponse.Success success -> new PaymentResult.Success(success.bankReference());
            case PaymentProcessResponse.Failure failure -> new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            case PaymentProcessResponse.Pending pending -> new PaymentResult.Pending(pending.processorReference());
        };
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("CARD_REF");

    }
}
