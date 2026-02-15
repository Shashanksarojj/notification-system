package com.notification.worker.sender;

import com.notification.worker.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class MockSmsSender implements NotificationSender {

    @Override
    public void send(Notification notification) throws Exception {

        Thread.sleep(500);
        System.out.println("SMS sent (mock): " + notification.getMessage());
    }
}
