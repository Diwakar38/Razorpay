package com.project.razorpay.merchant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant_webhook_config")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantWebhookConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Merchant merchant;

    @Column(name = "target_url", length = 500, nullable = false)
    private String targetUrl;

    @Column(name = "webhook_secret_hash", length = 200)
    private String webhookSecretHash;

    @Column(name = "enabled", nullable = false)
    private final Boolean enabled = true;

    @Column(length = 255)
    private String eventTypes;
}
