package com.example.notificationservice.provider;

import com.example.notificationservice.dto.NotificationMessage;

public interface EmailProvider {

    void send(NotificationMessage message);
}