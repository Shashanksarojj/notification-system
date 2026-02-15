package com.notification.worker.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DlqConsumer {

    @KafkaListener(topics = "notification-dlq", groupId = "dlq-group")
    public void consume(String message) {

        log.error("DLQ received notification id={}", message);
    }
}
