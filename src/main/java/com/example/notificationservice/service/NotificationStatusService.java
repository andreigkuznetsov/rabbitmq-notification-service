package com.example.notificationservice.service;

import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificationStatusService {

    private final NotificationRepository notificationRepository;

    public NotificationStatusService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void markProcessing(NotificationEntity entity) {
        entity.setStatus(NotificationStatus.PROCESSING);
        entity.setUpdatedAt(LocalDateTime.now());
        notificationRepository.save(entity);
    }

    @Transactional
    public void markSent(NotificationEntity entity) {
        entity.setStatus(NotificationStatus.SENT);
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setErrorCode(null);
        entity.setErrorMessage(null);
        notificationRepository.save(entity);
    }

    @Transactional
    public void markFailed(NotificationEntity entity, String errorCode, String errorMessage) {
        entity.setStatus(NotificationStatus.FAILED);
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setErrorCode(errorCode);
        entity.setErrorMessage(errorMessage);
        notificationRepository.save(entity);
    }
}