package com.example.notificationservice.entity;

import com.example.notificationservice.model.NotificationChannel;
import com.example.notificationservice.model.NotificationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "notification_id", nullable = false, unique = true)
    private String notificationId;

    @Setter
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationChannel channel;

    @Setter
    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Setter
    @Column(name = "template_code", nullable = false)
    private String templateCode;

    @Setter
    @Column(name = "payload_json", nullable = false, columnDefinition = "text")
    private String payloadJson;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status;

    @Setter
    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Setter
    @Column(name = "error_code")
    private String errorCode;

    @Setter
    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Setter
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Setter
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
