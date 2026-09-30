package com.project.razorpay.common.config;

import com.project.razorpay.common.enums.EventAggregateType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app.kafka")
@RequiredArgsConstructor
@Getter
@Setter
public class KafkaProperties {

    private Map<String, String> topics;

    public String topicFor(EventAggregateType type) {
        String topic = topics.get(type.name().toLowerCase());

        if (topic == null) {
            throw new IllegalStateException(String.format("No topic found for type %s", type.name().toLowerCase()));
        }
        return topic;
    }
}
