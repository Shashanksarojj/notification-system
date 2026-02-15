package com.notification.api.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "notification-events";

    public void publish(Long notificationId) {

        log.info("Publishing notification event to Kafka id={}", notificationId);
        kafkaTemplate.send(TOPIC, notificationId.toString());
    }
}
