package com.notification.worker.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;

    private String priority;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private String recipientEmail;   // NEW
    private String recipientPhone;

    private int retryCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
