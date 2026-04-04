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

        log.info("🚀 Starting email send for notificationId={}", notification.getId());

        // 🔍 Validate input
        if (notification.getRecipientEmail() == null) {
            log.error("❌ Recipient email missing for notificationId={}", notification.getId());
            throw new RuntimeException("Recipient email missing");
        }

        // 🔍 Log important fields
        String fromEmail = "shashankdw17@gmail.com";
        String toEmail = notification.getRecipientEmail();

        log.info("📧 FROM = {}", fromEmail);
        log.info("📧 TO = {}", toEmail);
        log.info("📝 MESSAGE = {}", notification.getMessage());

        // 🔐 Check API key presence (safe logging)
        if (apiKey == null || apiKey.isEmpty()) {
            log.error("❌ SendGrid API key is NULL or EMPTY");
            throw new RuntimeException("SendGrid API key missing");
        } else {
            log.info("🔑 API Key Loaded (starts with): {}", apiKey.substring(0, 5));
        }

        Email from = new Email(fromEmail, "Notification Service");
        String subject = "Notification";
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", notification.getMessage());

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        log.info("📤 Sending request to SendGrid for notificationId={}", notification.getId());

        Response response = sg.api(request);

        int status = response.getStatusCode();
        String responseBody = response.getBody();
        String responseHeaders = response.getHeaders().toString();

        // 🔍 Full response logging
        log.info("📥 SendGrid Response Status={} notificationId={}", status, notification.getId());
        log.debug("📥 SendGrid Response Body={}", responseBody);
        log.debug("📥 SendGrid Response Headers={}", responseHeaders);

        // ❌ Handle failures properly
        if (status == 401 || status == 403) {
            log.error("❌ AUTH ERROR: Invalid API key or sender not verified. notificationId={}", notification.getId());
        }

        if (status >= 400 && status < 500) {
            log.error("❌ CLIENT ERROR: status={} body={} notificationId={}", status, responseBody, notification.getId());
        }

        if (status >= 500) {
            log.error("❌ SERVER ERROR: SendGrid internal issue. status={} notificationId={}", status, notification.getId());
        }

        // 🔥 Throw exception to trigger retry/DLQ
        if (status < 200 || status >= 300) {
            throw new RuntimeException("SendGrid failed with status " + status);
        }

        log.info("✅ Email sent successfully for notificationId={}", notification.getId());
    }
}