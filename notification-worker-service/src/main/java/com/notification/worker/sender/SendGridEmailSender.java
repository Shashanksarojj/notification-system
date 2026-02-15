package com.notification.worker.sender;

import com.notification.worker.model.Notification;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendGridEmailSender implements NotificationSender {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Override
    public void send(Notification notification) throws Exception {

        if (notification.getRecipientEmail() == null) {
            throw new RuntimeException("Recipient email missing");
        }

        Email from = new Email("shashankdw1704@gmail.com");
        String subject = "Notification";
        Email to = new Email(notification.getRecipientEmail());
        Content content = new Content("text/plain", notification.getMessage());

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);

        int status = response.getStatusCode();

        log.info("SendGrid response status={} notificationId={}", status, notification.getId());

        // 🔥 IMPORTANT: trigger retry if not 2xx
        if (status < 200 || status >= 300) {
            log.error("SendGrid failed status={} body={}", status, response.getBody());
            throw new RuntimeException("SendGrid failed with status " + status);
        }
    }
}
