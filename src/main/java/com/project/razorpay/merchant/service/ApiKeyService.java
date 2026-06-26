package com.project.razorpay.merchant.service;

import com.project.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.project.razorpay.merchant.dto.response.CreateApiKeyResponse;
import jakarta.validation.Valid;

import java.util.UUID;

public interface ApiKeyService {
    CreateApiKeyResponse createApiKey(UUID merchantId,CreateApiKeyRequest request);
}
