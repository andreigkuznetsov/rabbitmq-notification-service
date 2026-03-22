package com.example.notificationservice.integration.worker;

import com.example.notificationservice.dto.NotificationMessage;
import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.integration.BaseIntegrationTest;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.support.TestDataFactory;
import com.example.notificationservice.model.TestRabbitConstants;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static com.example.notificationservice.model.NotificationStatus.SENT;
import static com.example.notificationservice.model.TestRabbitConstants.EMAIL_ROUTING_KEY;
import static com.example.notificationservice.model.TestRabbitConstants.EXCHANGE;
import static com.example.notificationservice.model.TestTemplateCodes.ORDER_CREATED;
import static org.assertj.core.api.Assertions.assertThat;

class EmailNotificationWorkerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void shouldConsumeMessageAndMarkNotificationAsSent() {
        String notificationId = TestDataFactory.randomNotificationId();

        NotificationEntity entity =
                TestDataFactory.queuedEmailEntity(notificationId, ORDER_CREATED);
        notificationRepository.save(entity);

        NotificationMessage message =
                TestDataFactory.emailMessage(notificationId, ORDER_CREATED);

        rabbitTemplate.convertAndSend(EXCHANGE, EMAIL_ROUTING_KEY, message);

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> {
                    NotificationEntity updated = notificationRepository.findByNotificationId(notificationId)
                            .orElseThrow(() -> new AssertionError("Notification not found: " + notificationId));

                    assertThat(updated.getStatus()).isEqualTo(SENT);
                    assertThat(updated.getRetryCount()).isZero();
                    assertThat(updated.getErrorCode()).isNull();
                    assertThat(updated.getErrorMessage()).isNull();
                    assertThat(updated.getRecipient()).isEqualTo("user@example.com");
                });
    }
}
