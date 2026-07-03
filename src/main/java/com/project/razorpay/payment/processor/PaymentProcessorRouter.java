package com.project.razorpay.payment.processor;

import com.project.razorpay.common.enums.PaymentMethod;
import com.project.razorpay.payment.processor.dto.PaymentProcessRequest;
import com.project.razorpay.payment.processor.dto.PaymentProcessResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentProcessorRouter {

    private Map<PaymentMethod, PaymentProcessor> paymentProcessors;

    public PaymentProcessResponse charge(PaymentProcessRequest request) {
        PaymentProcessor paymentProcessor = paymentProcessors.get(request.method());
        if(paymentProcessor == null) {
            throw new IllegalArgumentException("No payment processor registered for method!!" + request.method());
        }

        return paymentProcessor.charge(request);
    }
}
