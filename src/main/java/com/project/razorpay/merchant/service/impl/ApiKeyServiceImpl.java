package com.project.razorpay.merchant.service.impl;

import com.project.razorpay.common.exceptions.ResourceNotFoundException;
import com.project.razorpay.common.util.RandomizerUtil;
import com.project.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.project.razorpay.merchant.dto.response.ApiKeyRespose;
import com.project.razorpay.merchant.dto.response.CreateApiKeyResponse;
import com.project.razorpay.merchant.entity.ApiKey;
import com.project.razorpay.merchant.entity.Merchant;
import com.project.razorpay.merchant.repository.ApiKeyRepository;
import com.project.razorpay.merchant.repository.MerchantRepository;
import com.project.razorpay.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;
    private final MerchantRepository merchantRepository;

    @Override
    @Transactional
    public CreateApiKeyResponse createApiKey(UUID merchantId, CreateApiKeyRequest request) {
        log.info("Creating API Key for merchant: {}", merchantId);
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("MERCHANT", merchantId));

        String keyId = "rzp_" + request.environment().name().toUpperCase() + RandomizerUtil.randomBase64(24);

        String rawSecret = RandomizerUtil.randomBase64(40);

        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .keyId(keyId)
                .keySecretHash(rawSecret) // TODO: Ecode string later on
                .environment(request.environment())
                .build();

        apiKey = apiKeyRepository.save(apiKey);

        return new CreateApiKeyResponse(apiKey.getId(), keyId, rawSecret, request.environment());
    }

    @Override
    public List<ApiKeyRespose> listByMerchant(UUID merchantId) {
        return apiKeyRepository.findByMerchant_Id(merchantId).stream()
                .map(apiKey -> new ApiKeyRespose(
                        apiKey.getId(),
                        apiKey.getKeyId(),
                        apiKey.getEnvironment(),
                        apiKey.isEnabled(),
                        apiKey.getLastUsedAt(),
                        null))
                .toList();
    }

    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKey key = apiKeyRepository.findById(keyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("Apikey", keyId));
        key.setEnabled(false);
        apiKeyRepository.save(key);
    }

    @Override
    @Transactional
    public CreateApiKeyResponse rotateKey(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("Apikey", keyId));

        if(!apiKey.isEnabled()) throw new RuntimeException("Cannot rotate a disabled key");

        String newRawSecret = RandomizerUtil.randomBase64(40);
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(newRawSecret); // TODO: Ecode string later on
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiresAt(LocalDateTime.now().plusHours(24));
        apiKey = apiKeyRepository.save(apiKey);

        return new CreateApiKeyResponse(apiKey.getId(), apiKey.getKeyId(), apiKey.getKeySecretHash(), apiKey.getEnvironment());
    }
}
