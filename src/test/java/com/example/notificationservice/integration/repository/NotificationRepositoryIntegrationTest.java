package com.example.notificationservice.integration.repository;

import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.integration.BaseIntegrationTest;
import com.example.notificationservice.model.NotificationChannel;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void shouldSaveAndFindByNotificationId() {
        String notificationId = "NTF-" + UUID.randomUUID();

        NotificationEntity entity = new NotificationEntity();
        entity.setNotificationId(notificationId);
        entity.setUserId("USR-77");
        entity.setChannel(NotificationChannel.EMAIL);
        entity.setRecipient("user@example.com");
        entity.setTemplateCode("ORDER_CREATED");
        entity.setPayloadJson("{\"orderId\":\"ORD-555\",\"amount\":1200}");
        entity.setStatus(NotificationStatus.QUEUED);
        entity.setRetryCount(0);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        notificationRepository.save(entity);

        Optional<NotificationEntity> saved = notificationRepository.findByNotificationId(notificationId);

        assertThat(saved).isPresent();
        assertThat(saved.get().getNotificationId()).isEqualTo(notificationId);
        assertThat(saved.get().getChannel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(saved.get().getStatus()).isEqualTo(NotificationStatus.QUEUED);
        assertThat(saved.get().getRecipient()).isEqualTo("user@example.com");
    }
}