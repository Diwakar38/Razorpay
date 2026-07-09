package com.project.razorpay.payment.gateway;

import com.project.razorpay.payment.gateway.dto.request.PaymentRequest;
import com.project.razorpay.payment.gateway.dto.response.PaymentResult;

import java.util.UUID;

public interface PaymentAdapter {
    PaymentResult initiate(PaymentRequest request);

    PaymentResult capture(UUID paymentId);
}
