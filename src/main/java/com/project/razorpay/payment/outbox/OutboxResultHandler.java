package com.project.razorpay.payment.outbox;

import com.project.razorpay.common.enums.OutboxStatus;
import com.project.razorpay.merchant.entity.OutboxEvent;
import com.project.razorpay.merchant.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OutboxResultHandler {
    private final OutboxEventRepository outboxEventRepository;
    private final Integer MAX_ATTEMPTS = 3;

    @Transactional
    public void handleEventPublished(OutboxEvent event) {
        event.setStatus(OutboxStatus.PUBLISHED);
        event.setPublishedAt(LocalDateTime.now());
        outboxEventRepository.save(event);
    }

    @Transactional
    public void handleEventFailed(OutboxEvent event, String message) {
        event.setAttempts(event.getAttempts() + 1);
        event.setLastError(
                message.length() < 1000 ? message :message.substring(0, 1000));
        if (event.getAttempts() >= MAX_ATTEMPTS) {
            event.setStatus(OutboxStatus.FAILED);
        }
        outboxEventRepository.save(event);
    }
}
