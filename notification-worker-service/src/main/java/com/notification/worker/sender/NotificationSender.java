package com.notification.worker.sender;

import com.notification.worker.model.Notification;

public interface NotificationSender {

    void send(Notification notification) throws Exception;
}
