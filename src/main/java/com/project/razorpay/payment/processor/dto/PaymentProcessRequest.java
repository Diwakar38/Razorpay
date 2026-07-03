package com.project.razorpay.payment.processor.dto;

import com.project.razorpay.common.entity.Money;
import com.project.razorpay.common.enums.PaymentMethod;

import java.util.Map;

public record PaymentProcessRequest(
        PaymentMethod method,
        Money amount,
        Map<String, Object> methodDetails
) {
}
