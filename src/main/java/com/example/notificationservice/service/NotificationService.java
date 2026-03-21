package com.example.notificationservice.service;

import com.example.notificationservice.dto.CreateNotificationRequest;
import com.example.notificationservice.dto.CreateNotificationResponse;
import com.example.notificationservice.dto.NotificationMessage;
import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.messaging.NotificationPublisher;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.notificationservice.exception.DuplicateNotificationException;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private final NotificationValidationService validationService;
    private final NotificationRepository notificationRepository;
    private final NotificationPublisher notificationPublisher;
    private final ObjectMapper objectMapper;

    public NotificationService(
            NotificationValidationService validationService,
            NotificationRepository notificationRepository,
            NotificationPublisher notificationPublisher,
            ObjectMapper objectMapper
    ) {
        this.validationService = validationService;
        this.notificationRepository = notificationRepository;
        this.notificationPublisher = notificationPublisher;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public CreateNotificationResponse createNotification(CreateNotificationRequest request) {
        validationService.validate(request);

        if (notificationRepository.existsByNotificationId(request.getNotificationId())) {
            throw new DuplicateNotificationException(
                    "Notification already exists: " + request.getNotificationId()
            );
        }

        NotificationEntity entity = new NotificationEntity();
        entity.setNotificationId(request.getNotificationId());
        entity.setUserId(request.getUserId());
        entity.setChannel(request.getChannel());
        entity.setRecipient(request.getRecipient());
        entity.setTemplateCode(request.getTemplateCode());
        entity.setPayloadJson(toJson(request));
        entity.setStatus(NotificationStatus.QUEUED);
        entity.setRetryCount(0);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        notificationRepository.save(entity);

        NotificationMessage message = new NotificationMessage();
        message.setNotificationId(request.getNotificationId());
        message.setUserId(request.getUserId());
        message.setChannel(request.getChannel());
        message.setRecipient(request.getRecipient());
        message.setTemplateCode(request.getTemplateCode());
        message.setPayload(request.getPayload());

        notificationPublisher.publish(message);

        return new CreateNotificationResponse(
                request.getNotificationId(),
                NotificationStatus.QUEUED.name(),
                "Notification accepted for processing"
        );
    }

    private String toJson(CreateNotificationRequest request) {
        try {
            return objectMapper.writeValueAsString(request.getPayload());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize notification payload", e);
        }
    }
}