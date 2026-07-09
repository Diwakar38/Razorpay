package com.project.razorpay.payment.processor.strategy;

import com.project.razorpay.common.util.RandomizerUtil;
import com.project.razorpay.payment.processor.PaymentProcessor;
import com.project.razorpay.payment.processor.dto.PaymentProcessRequest;
import com.project.razorpay.payment.processor.dto.PaymentProcessResponse;

public class CardPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessResponse charge(PaymentProcessRequest request) {
        return null;
    }
}
