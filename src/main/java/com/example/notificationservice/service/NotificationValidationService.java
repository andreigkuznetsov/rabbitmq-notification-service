package com.example.notificationservice.service;

import com.example.notificationservice.dto.CreateNotificationRequest;
import com.example.notificationservice.exception.NotificationValidationException;
import com.example.notificationservice.model.NotificationChannel;
import org.springframework.stereotype.Service;

@Service
public class NotificationValidationService {

    public void validate(CreateNotificationRequest request) {
        if (request.getChannel() != NotificationChannel.EMAIL) {
            throw new NotificationValidationException("Only EMAIL channel is supported in MVP");
        }
    }
}
