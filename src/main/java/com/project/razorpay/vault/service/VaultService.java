package com.project.razorpay.vault.service;

import com.project.razorpay.common.entity.Money;
import com.project.razorpay.payment.processor.dto.PaymentProcessResponse;
import com.project.razorpay.vault.dto.request.TokenizeRequest;
import com.project.razorpay.vault.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

public interface VaultService {
    TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId);

    PaymentProcessResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails);
}
