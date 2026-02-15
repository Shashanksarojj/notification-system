package com.notification.worker.sender;

import com.notification.worker.model.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationSenderFactory {

    private final SendGridEmailSender emailSender;
    private final MockSmsSender smsSender;

    public NotificationSender getSender(NotificationType type) {

        return switch (type) {
            case EMAIL -> emailSender;
            case SMS -> smsSender;
            case PUSH -> smsSender;
        };
    }
}
