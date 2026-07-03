package com.project.razorpay.payment.processor;

import com.project.razorpay.payment.processor.dto.PaymentProcessRequest;
import com.project.razorpay.payment.processor.dto.PaymentProcessResponse;

public interface PaymentProcessor {

    PaymentProcessResponse charge(PaymentProcessRequest request);
}
