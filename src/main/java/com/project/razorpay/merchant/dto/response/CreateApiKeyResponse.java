package com.project.razorpay.merchant.dto.response;

import java.util.UUID;

public record CreateApiKeyResponse(
        UUID id,
        String keyId,
        String keySecret,
        com.project.razorpay.common.enums.Environment environment
) {
}
