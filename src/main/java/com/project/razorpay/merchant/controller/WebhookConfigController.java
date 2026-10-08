package com.project.razorpay.merchant.controller;

import com.project.razorpay.merchant.dto.request.UpdateWebhookConfigRequest;
import com.project.razorpay.merchant.dto.response.WebhookConfigResponse;
import com.project.razorpay.merchant.security.MerchantContext;
import com.project.razorpay.merchant.service.WebhookConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/merchants/webhooks")
@RequiredArgsConstructor
public class WebhookConfigController {

    private final WebhookConfigService webhookConfigService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<WebhookConfigResponse> create(@Valid @RequestBody UpdateWebhookConfigRequest request) {
        return ResponseEntity.ok(webhookConfigService.create(merchantContext.getMerchantId(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebhookConfigResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(webhookConfigService.getById(merchantContext.getMerchantId(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebhookConfigResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWebhookConfigRequest request) {
        return ResponseEntity.ok(webhookConfigService.update(merchantContext.getMerchantId(),id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        webhookConfigService.delete(merchantContext.getMerchantId(),id);
        return ResponseEntity.noContent().build();
    }

}
