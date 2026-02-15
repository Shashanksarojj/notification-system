package com.notification.worker.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkerKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String MAIN_TOPIC = "notification-events";
    private static final String DLQ_TOPIC = "notification-dlq";

    public void retry(Long id) {
        log.warn("Retrying notification id={}", id);
        kafkaTemplate.send(MAIN_TOPIC, id.toString());
    }

    public void sendToDlq(Long id) {
        log.error("Sending notification to DLQ id={}", id);
        kafkaTemplate.send(DLQ_TOPIC, id.toString());
    }
}
