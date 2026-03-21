package com.example.notificationservice.dto;

import com.example.notificationservice.model.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class CreateNotificationRequest {

    @NotBlank
    private String notificationId;

    @NotBlank
    private String userId;

    @NotNull
    private NotificationChannel channel;

    @NotBlank
    private String recipient;

    @NotBlank
    private String templateCode;

    @NotNull
    private Map<String, Object> payload;

}
