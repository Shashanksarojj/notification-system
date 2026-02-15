package com.notification.api.controller;

import com.notification.api.dto.CreateNotificationRequest;
import com.notification.api.dto.NotificationResponse;
import com.notification.api.model.Notification;
import com.notification.api.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public NotificationResponse create(@Valid @RequestBody CreateNotificationRequest request) {
        log.info("Incoming notification request userId={} type={}",
                request.getUserId(), request.getType());
        return service.createNotification(request);
    }

    @GetMapping("/{id}")
    public NotificationResponse getById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @GetMapping("/user/{userId}")
    public List<NotificationResponse> getByUser(@PathVariable("userId") Long userId) {
        return service.getByUser(userId);
    }

}
