package com.project.razorpay.payment.gateway;

import com.project.razorpay.common.enums.PaymentMethod;
import com.project.razorpay.payment.gateway.dto.request.PaymentRequest;
import com.project.razorpay.payment.gateway.dto.response.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentGatewayRouter {

    private final Map<PaymentMethod, PaymentAdapter> paymentAdapters;

    public PaymentResult initiate(PaymentRequest request) {
        PaymentAdapter adapter = paymentAdapters.get(request.method());
        if(adapter == null) {
            throw new IllegalArgumentException("No payment adapter registered for method!!" + request.method());
        }
        return adapter.initiate(request);
    }
}
