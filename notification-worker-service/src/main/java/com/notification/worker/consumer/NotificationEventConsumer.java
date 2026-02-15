package com.notification.worker.consumer;

import com.notification.worker.service.NotificationProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final ExecutorService executor;
    private final NotificationProcessorService processor;

    @KafkaListener(topics = "notification-events", groupId = "notification-worker-group")
    public void consume(String message) {

        Long notificationId = Long.parseLong(message);

        log.info("Received Kafka event notificationId={}", notificationId);

        executor.submit(() -> processor.process(notificationId));
    }
}
