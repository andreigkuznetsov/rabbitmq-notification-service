package com.example.notificationservice.integration.controller;

import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.integration.BaseIntegrationTest;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.support.TestDataFactory;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.Map;

import static com.example.notificationservice.model.TestTemplateCodes.ORDER_CREATED;
import static com.example.notificationservice.model.TestTemplateCodes.QUEUED;
import static org.assertj.core.api.Assertions.assertThat;

class NotificationControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldAcceptRequestAndCreateNotificationRecord() {
        String notificationId = TestDataFactory.randomNotificationId();
        Map<String, Object> requestBody =
                TestDataFactory.emailRequestBody(notificationId, ORDER_CREATED);

        ResponseEntity<String> response = notificationClient.createNotification(requestBody);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).contains(notificationId);
        assertThat(response.getBody()).contains(QUEUED);

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    NotificationEntity entity = notificationRepository.findByNotificationId(notificationId)
                            .orElseThrow(() -> new AssertionError("Notification not found: " + notificationId));

                    assertThat(entity.getNotificationId()).isEqualTo(notificationId);
                    assertThat(entity.getStatus()).isIn(
                            NotificationStatus.QUEUED,
                            NotificationStatus.PROCESSING,
                            NotificationStatus.SENT
                    );
                    assertThat(entity.getRecipient()).isEqualTo("user@example.com");
                });
    }
}