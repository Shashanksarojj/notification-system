package com.notification.worker.service;

import com.notification.worker.model.*;
import com.notification.worker.queue.WorkerKafkaProducer;
import com.notification.worker.repository.NotificationRepository;
import com.notification.worker.sender.NotificationSender;
import com.notification.worker.sender.NotificationSenderFactory;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
public class NotificationProcessorService {

    private final NotificationRepository repository;
    private final NotificationSenderFactory senderFactory;
    private final WorkerKafkaProducer kafkaProducer;
    private final NotificationStatusLogService logService;

    private final Counter sentCounter;
    private final Counter failedCounter;
    private final Counter retryCounter;

    public NotificationProcessorService(
            NotificationRepository repository,
            NotificationSenderFactory senderFactory,
            WorkerKafkaProducer kafkaProducer,
            NotificationStatusLogService logService,
            MeterRegistry meterRegistry
    ) {
        this.repository = repository;
        this.senderFactory = senderFactory;
        this.kafkaProducer = kafkaProducer;
        this.logService = logService;

        this.sentCounter = meterRegistry.counter("notifications.sent");
        this.failedCounter = meterRegistry.counter("notifications.failed");
        this.retryCounter = meterRegistry.counter("notifications.retried");
    }




    public void process(Long notificationId) {

        Notification n = repository.findById(notificationId).orElse(null);
        if (n == null) return;

        try {
            log.info("Processing notification id={}", notificationId);

            n.setStatus(NotificationStatus.PROCESSING);
            repository.save(n);

            logService.log(notificationId, "PROCESSING", null);

            NotificationSender sender = senderFactory.getSender(n.getType());
            sender.send(n);

            n.setStatus(NotificationStatus.SENT);
            repository.save(n);

            logService.log(notificationId, "SENT", null);

            sentCounter.increment();   // ✅ metrics

            log.info("Notification sent successfully id={}", notificationId);

        } catch (Exception e) {

            log.error("Notification failed id={}", notificationId, e);

            n.setRetryCount(n.getRetryCount() + 1);
            repository.save(n);

            logService.log(notificationId, "FAILED", e.getMessage());

            if (n.getRetryCount() < 3) {

                retryCounter.increment();   // ✅ metrics
                kafkaProducer.retry(notificationId);

                logService.log(notificationId, "RETRY", null);

            } else {

                n.setStatus(NotificationStatus.FAILED);
                repository.save(n);

                failedCounter.increment();  // ✅ metrics
                kafkaProducer.sendToDlq(notificationId);

                logService.log(notificationId, "DLQ", e.getMessage());
            }
        }
    }

    private void simulateSend(Notification n) throws Exception {

        Thread.sleep(1000);

        // random failure
        if (new Random().nextInt(5) == 0) {
            throw new RuntimeException("Random failure");
        }
    }
}
