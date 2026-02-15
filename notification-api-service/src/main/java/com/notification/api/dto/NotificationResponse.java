package com.notification.api.dto;

import com.notification.api.model.NotificationStatus;
import com.notification.api.model.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private Long id;
    private Long userId;
    private NotificationType type;
    private String message;
    private String priority;
    private NotificationStatus status;
    private int retryCount;
    private LocalDateTime createdAt;
}
