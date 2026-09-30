package com.project.razorpay.payment.outbox;

import com.project.razorpay.common.config.KafkaProperties;
import com.project.razorpay.common.enums.OutboxStatus;
import com.project.razorpay.merchant.entity.OutboxEvent;
import com.project.razorpay.merchant.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPoller {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final OutboxResultHandler outboxResultHandler;

    @Scheduled(fixedRate = 10000)
    public void poll() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        log.info("Found {} new entries", pendingEvents.size());
        for (OutboxEvent event : pendingEvents) {
            try {
                String topic = kafkaProperties.topicFor(event.getAggregateType());
                String key = extractMerchantId(event.getPayload());

                Map<String, Object> envelope = Map.of(
                        "eventType", event.getEventType(),
                        "aggregateType", event.getAggregateType().name(),
                        "aggregateId", event.getAggregateId().toString(),
                        "data", event.getPayload()
                );

                kafkaTemplate.send(topic,key,envelope)
                        .get(5, TimeUnit.SECONDS);

                outboxResultHandler.handleEventPublished(event);
            } catch (Exception e) {
                log.info("Error while polling outbox event, eventId:{}, attempts:{}", event.getId().toString(), event.getAttempts());
                outboxResultHandler.handleEventFailed(event, e.getMessage());
            }
        }
    }

    private String extractMerchantId(Map<String, Object> payload) {
        Object value = payload.get("merchantId");
        return value != null ? value.toString() : "unknown";
    }
}
