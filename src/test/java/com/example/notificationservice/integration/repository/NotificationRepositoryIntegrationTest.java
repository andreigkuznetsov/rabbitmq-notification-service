package com.example.notificationservice.integration.repository;

import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.integration.BaseIntegrationTest;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.example.notificationservice.model.TestTemplateCodes.ORDER_CREATED;
import static org.assertj.core.api.Assertions.assertThat;

class NotificationRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void shouldSaveAndFindByNotificationId() {
        String notificationId = TestDataFactory.randomNotificationId();

        NotificationEntity entity =
                TestDataFactory.queuedEmailEntity(notificationId, ORDER_CREATED);

        notificationRepository.save(entity);

        Optional<NotificationEntity> saved = notificationRepository.findByNotificationId(notificationId);

        assertThat(saved).isPresent();
        assertThat(saved.get().getNotificationId()).isEqualTo(notificationId);
        assertThat(saved.get().getChannel()).isEqualTo(entity.getChannel());
        assertThat(saved.get().getStatus()).isEqualTo(entity.getStatus());
        assertThat(saved.get().getRecipient()).isEqualTo(entity.getRecipient());
        assertThat(saved.get().getTemplateCode()).isEqualTo(entity.getTemplateCode());
        assertThat(saved.get().getPayloadJson()).isEqualTo(entity.getPayloadJson());
    }
}