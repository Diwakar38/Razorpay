package com.project.razorpay.merchant.dto.request;

import com.project.razorpay.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment

) {
}
