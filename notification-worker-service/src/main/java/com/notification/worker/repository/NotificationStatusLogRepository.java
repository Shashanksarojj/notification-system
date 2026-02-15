package com.notification.worker.repository;


import com.notification.worker.model.NotificationStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationStatusLogRepository
        extends JpaRepository<NotificationStatusLog, Long> {
}
