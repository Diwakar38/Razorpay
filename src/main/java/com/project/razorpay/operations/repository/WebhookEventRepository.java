package com.project.razorpay.operations.repository;

import com.project.razorpay.common.enums.WebHookEventStatus;
import com.project.razorpay.operations.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {
    List<WebhookEvent> findByStatusAndNextRetryAtBefore(WebHookEventStatus webHookEventStatus, LocalDateTime now);
}
