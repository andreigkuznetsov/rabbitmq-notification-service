package com.example.notificationservice.integration.worker;

import com.example.notificationservice.dto.NotificationMessage;
import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.integration.BaseIntegrationTest;
import com.example.notificationservice.model.NotificationErrorCode;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.support.TestDataFactory;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static com.example.notificationservice.model.TestRabbitConstants.EMAIL_ROUTING_KEY;
import static com.example.notificationservice.model.TestRabbitConstants.EXCHANGE;
import static com.example.notificationservice.model.TestTemplateCodes.FORCE_RETRY;
import static org.assertj.core.api.Assertions.assertThat;

class RetryFlowIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void shouldMarkNotificationForRetryOnTemporaryProviderFailure() {
        String notificationId = TestDataFactory.randomNotificationId();

        NotificationEntity entity =
                TestDataFactory.queuedEmailEntity(notificationId, FORCE_RETRY);
        notificationRepository.save(entity);

        NotificationMessage message =
                TestDataFactory.emailMessage(notificationId, FORCE_RETRY);

        rabbitTemplate.convertAndSend(EXCHANGE, EMAIL_ROUTING_KEY, message);

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> {
                    NotificationEntity updated = notificationRepository.findByNotificationId(notificationId)
                            .orElseThrow(() -> new AssertionError("Notification not found: " + notificationId));

                    assertThat(updated.getStatus()).isEqualTo(NotificationStatus.RETRY);
                    assertThat(updated.getRetryCount()).isEqualTo(1);
                    assertThat(updated.getErrorCode()).isEqualTo(NotificationErrorCode.RETRYABLE_PROVIDER_ERROR.name());
                    assertThat(updated.getErrorMessage()).contains("Temporary email provider failure");
                });
    }
}