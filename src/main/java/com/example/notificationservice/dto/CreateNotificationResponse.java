package com.example.notificationservice.dto;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class CreateNotificationResponse {

    private String notificationId;
    private String status;
    private String message;

    public CreateNotificationResponse() {
    }

    public CreateNotificationResponse(String notificationId, String status, String message) {
        this.notificationId = notificationId;
        this.status = status;
        this.message = message;
    }

}