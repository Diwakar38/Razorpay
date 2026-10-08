package com.project.razorpay.merchant.service.impl;

import com.project.razorpay.common.exceptions.ResourceNotFoundException;
import com.project.razorpay.common.util.RandomizerUtil;
import com.project.razorpay.merchant.api.MerchantWebhookApi;
import com.project.razorpay.merchant.dto.request.UpdateWebhookConfigRequest;
import com.project.razorpay.merchant.dto.response.WebhookConfigResponse;
import com.project.razorpay.common.dto.WebhookTarget;
import com.project.razorpay.merchant.entity.Merchant;
import com.project.razorpay.merchant.entity.MerchantWebhookConfig;
import com.project.razorpay.merchant.mapper.WebhookConfigMapper;
import com.project.razorpay.merchant.repository.MerchantRepository;
import com.project.razorpay.merchant.repository.WebhookConfigRepository;
import com.project.razorpay.merchant.service.WebhookConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookConfigServiceImpl implements WebhookConfigService, MerchantWebhookApi {
    private final WebhookConfigRepository webhookConfigRepository;
    private final MerchantRepository merchantRepository;
    private final BytesEncryptor bytesEncryptor;
    private final WebhookConfigMapper webhookConfigMapper;

    @Override
    public WebhookConfigResponse create(UUID merchantId, UpdateWebhookConfigRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", merchantId));

        String rawSecret = RandomizerUtil.randomBase64(32);
        byte[] rawSecretBytes = rawSecret.getBytes(StandardCharsets.UTF_8);
        String encryptedSecret = Base64.getEncoder().encodeToString(
                bytesEncryptor.encrypt(rawSecretBytes));

        MerchantWebhookConfig config = MerchantWebhookConfig.builder()
                .merchant(merchant)
                .targetUrl(request.targetUrl())
                .webhookSecret(encryptedSecret)
                .eventTypes(request.eventTypes())
                .build();

        config = webhookConfigRepository.save(config);

        return webhookConfigMapper.toResponse(config, rawSecret);
    }

    @Override
    public List<WebhookConfigResponse> list(UUID merchantId) {
        return webhookConfigRepository.findByMerchant_Id(merchantId)
                .stream()
                .map(config -> webhookConfigMapper.toResponse(config, null))
                .toList();
    }

    @Override
    public WebhookConfigResponse getById(UUID merchantId, UUID configId) {
        MerchantWebhookConfig config = requireOwnedConfig(merchantId, configId);
        return webhookConfigMapper.toResponse(config, null);
    }

    @Override
    public WebhookConfigResponse update(UUID merchantId, UUID configId, UpdateWebhookConfigRequest request) {
        MerchantWebhookConfig config = requireOwnedConfig(merchantId, configId);
        config.setEventTypes(request.eventTypes());
        config.setTargetUrl(request.targetUrl());
        log.info("Merchant webhook config updated id:{}, merchantId:{}", configId, merchantId);
        webhookConfigRepository.save(config);
        return webhookConfigMapper.toResponse(config,null);
    }

    @Override
    public void delete(UUID merchantId, UUID configId) {
        MerchantWebhookConfig config = requireOwnedConfig(merchantId, configId);
        webhookConfigRepository.delete(config);
        log.info("Merchant webhook config deleted id:{}, merchantId:{}", configId, merchantId);
    }

    private MerchantWebhookConfig requireOwnedConfig(UUID merchantId, UUID configId) {
        return webhookConfigRepository.findByIdAndMerchant_Id(configId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("MerchantWebhookConfig", configId));
    }

    @Override
    public List<WebhookTarget> getActiveConfigForEvent(UUID merchantId, String eventType) {
        return webhookConfigRepository.findByMerchant_IdAndEnabledTrue(merchantId)
                .stream()
                .filter(config -> config.isSubscibedTo(eventType))
                .map(config -> {
                    byte[] cipherBytes = Base64.getDecoder().decode(config.getWebhookSecret());
                    byte[] decryptedSecretBytes = bytesEncryptor.decrypt(cipherBytes);
                    return new WebhookTarget(config.getId(), config.getTargetUrl(),
                                             new String(decryptedSecretBytes, StandardCharsets.UTF_8));
                })
                .toList();
    }
}
