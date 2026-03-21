package com.example.notificationservice.integration.worker;

import com.example.notificationservice.dto.NotificationMessage;
import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.integration.BaseIntegrationTest;
import com.example.notificationservice.model.NotificationChannel;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RetryFlowIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void shouldMarkNotificationForRetryOnTemporaryProviderFailure() {
        String notificationId = "NTF-" + UUID.randomUUID();

        NotificationEntity entity = new NotificationEntity();
        entity.setNotificationId(notificationId);
        entity.setUserId("USR-77");
        entity.setChannel(NotificationChannel.EMAIL);
        entity.setRecipient("user@example.com");
        entity.setTemplateCode("FORCE_RETRY");
        entity.setPayloadJson("{\"orderId\":\"ORD-555\",\"amount\":1200}");
        entity.setStatus(NotificationStatus.QUEUED);
        entity.setRetryCount(0);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        notificationRepository.save(entity);

        NotificationMessage message = new NotificationMessage();
        message.setNotificationId(notificationId);
        message.setUserId("USR-77");
        message.setChannel(NotificationChannel.EMAIL);
        message.setRecipient("user@example.com");
        message.setTemplateCode("FORCE_RETRY");
        message.setPayload(Map.of(
                "orderId", "ORD-555",
                "amount", 1200
        ));

        rabbitTemplate.convertAndSend("notification.exchange", "notification.email", message);

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> {
                    NotificationEntity updated = notificationRepository.findByNotificationId(notificationId)
                            .orElseThrow(() -> new AssertionError("Notification not found: " + notificationId));

                    assertThat(updated.getStatus()).isEqualTo(NotificationStatus.RETRY);
                    assertThat(updated.getRetryCount()).isEqualTo(1);
                    assertThat(updated.getErrorCode()).isEqualTo("RETRYABLE_PROVIDER_ERROR");
                    assertThat(updated.getErrorMessage()).contains("Temporary email provider failure");
                });
    }
}