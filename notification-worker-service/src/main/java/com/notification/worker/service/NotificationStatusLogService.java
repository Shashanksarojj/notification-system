package com.notification.worker.service;

import com.notification.worker.model.NotificationStatusLog;
import com.notification.worker.repository.NotificationStatusLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationStatusLogService {

    private final NotificationStatusLogRepository repo;

    public void log(Long notificationId, String status, String error) {

        NotificationStatusLog log = NotificationStatusLog.builder()
                .notificationId(notificationId)
                .status(status)
                .errorMessage(error)
                .createdAt(LocalDateTime.now())
                .build();

        repo.save(log);
    }
}
