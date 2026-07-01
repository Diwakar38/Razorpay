package com.project.razorpay.merchant.service;

import com.project.razorpay.merchant.dto.request.ApiKeyCreateRequest;
import com.project.razorpay.merchant.dto.response.ApiKeyRespose;
import com.project.razorpay.merchant.dto.response.ApiKeyCreateResponse;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {
    ApiKeyCreateResponse createApiKey(UUID merchantId, ApiKeyCreateRequest request);

    List<ApiKeyRespose> listByMerchant(UUID merchantId);

    void revoke(UUID merchantId, UUID keyId);

    ApiKeyCreateResponse rotateKey(UUID merchantId, UUID keyId);
}
