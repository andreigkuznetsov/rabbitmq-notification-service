package com.example.notificationservice.dto;

import com.example.notificationservice.model.NotificationChannel;

import java.util.Map;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class NotificationMessage {

    private String notificationId;
    private String userId;
    private NotificationChannel channel;
    private String recipient;
    private String templateCode;
    private Map<String, Object> payload;

    public NotificationMessage() {
    }

}
