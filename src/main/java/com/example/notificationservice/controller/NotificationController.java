package com.example.notificationservice.controller;

import com.example.notificationservice.dto.CreateNotificationRequest;
import com.example.notificationservice.dto.CreateNotificationResponse;
import com.example.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CreateNotificationResponse create(@Valid @RequestBody CreateNotificationRequest request) {
        return notificationService.createNotification(request);
    }
}