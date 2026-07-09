package com.project.razorpay.payment.gateway.adapter;

import com.project.razorpay.payment.gateway.PaymentAdapter;
import com.project.razorpay.payment.gateway.dto.request.PaymentRequest;
import com.project.razorpay.payment.gateway.dto.response.PaymentResult;

import java.util.UUID;

public class CardPaymentAdapter implements PaymentAdapter {
    @Override
    public PaymentResult initiate(PaymentRequest request) {
        return null;
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return null;
    }
}
