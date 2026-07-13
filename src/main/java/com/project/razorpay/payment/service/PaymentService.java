package com.project.razorpay.payment.service;

import com.project.razorpay.payment.dto.request.PaymentInitRequest;
import com.project.razorpay.payment.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse initiate(UUID merchantId, PaymentInitRequest request);

    PaymentResponse capture(UUID merchantId, UUID paymentId);

    void reslveAuthorization(UUID paymentId, boolean approve, String bankRef, String errorCode, String errorDescription);
}
