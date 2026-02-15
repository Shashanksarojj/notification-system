package com.notification.api.service;

import com.notification.api.dto.CreateNotificationRequest;
import com.notification.api.dto.NotificationResponse;
import com.notification.api.exception.DuplicateException;
import com.notification.api.exception.RateLimitException;
import com.notification.api.model.*;
import com.notification.api.queue.NotificationEventProducer;
import com.notification.api.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationEventProducer producer;
    private final RateLimiterService rateLimiter;
    private final DedupService dedupService;


    public NotificationResponse createNotification(CreateNotificationRequest req) {

        log.info("Creating notification for userId={} type={}",
                req.getUserId(), req.getType());

        if (!rateLimiter.isAllowed(req.getUserId())) {
            throw new RateLimitException("Rate limit exceeded");
        }

        if (dedupService.isDuplicate(req.getUserId(), req.getMessage())) {
            throw new DuplicateException("Duplicate notification");
        }

        Notification notification = Notification.builder()
                .userId(req.getUserId())
                .type(NotificationType.valueOf(req.getType()))
                .message(req.getMessage())
                .recipientEmail(req.getRecipientEmail())   // NEW
                .recipientPhone(req.getRecipientPhone())
                .priority(req.getPriority())
                .status(NotificationStatus.PENDING)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Notification saved = repository.save(notification);
        log.info("Notification saved id={}", saved.getId());
        log.info("Publishing event to Kafka id={}", saved.getId());

        producer.publish(saved.getId());

        return map(saved);
    }

    public NotificationResponse getById(Long id) {
        Notification n = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        return map(n);
    }

    public List<NotificationResponse> getByUser(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private NotificationResponse map(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .userId(n.getUserId())
                .type(n.getType())
                .message(n.getMessage())
                .priority(n.getPriority())
                .status(n.getStatus())
                .retryCount(n.getRetryCount())
                .createdAt(n.getCreatedAt())
                .build();
    }


}
