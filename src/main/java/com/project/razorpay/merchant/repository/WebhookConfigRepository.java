package com.project.razorpay.merchant.repository;

import com.project.razorpay.merchant.dto.response.WebhookConfigResponse;
import com.project.razorpay.merchant.entity.MerchantWebhookConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookConfigRepository extends JpaRepository<MerchantWebhookConfig, UUID> {
    List<MerchantWebhookConfig> findByMerchant_Id(UUID merchantId);

    List<MerchantWebhookConfig> findByMerchant_IdAndEnabledTrue(UUID merchantId);

    Optional<MerchantWebhookConfig> findByIdAndMerchant_Id(UUID configId, UUID merchantId);
}
