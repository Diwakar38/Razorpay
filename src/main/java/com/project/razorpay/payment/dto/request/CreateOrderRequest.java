package com.project.razorpay.payment.dto.request;

import com.project.razorpay.common.entity.Money;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Map;

public record CreateOrderRequest(
        @NotNull(message = "Amount is required")
        Money amount,

        @Size(max = 100)
        String receipt, // Order Id known to merchant

        Map<String, Object> notes,

        LocalDateTime expiresAt
) {
}
