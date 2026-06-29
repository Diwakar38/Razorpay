package com.project.razorpay.merchant.service;

import com.project.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.project.razorpay.merchant.dto.response.ApiKeyRespose;
import com.project.razorpay.merchant.dto.response.CreateApiKeyResponse;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {
    CreateApiKeyResponse createApiKey(UUID merchantId, CreateApiKeyRequest request);

    List<ApiKeyRespose> listByMerchant(UUID merchantId);

    void revoke(UUID merchantId, UUID keyId);

    CreateApiKeyResponse rotateKey(UUID merchantId, UUID keyId);
}
