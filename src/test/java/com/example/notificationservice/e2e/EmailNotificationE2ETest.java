package com.example.notificationservice.e2e;

import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.notificationservice.support.TestDataFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.example.notificationservice.model.TestTemplateCodes.ORDER_CREATED;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Map;

class EmailNotificationE2ETest extends BaseE2ETest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void shouldProcessEmailNotificationSuccessfully() {
        String notificationId = TestDataFactory.randomNotificationId();
        Map<String, Object> requestBody =
                TestDataFactory.emailRequestBody(notificationId, ORDER_CREATED);

        ResponseEntity<String> response = notificationClient.createNotification(requestBody);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).contains(notificationId);
        assertThat(response.getBody()).contains(NotificationStatus.QUEUED.name());

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> {
                    NotificationEntity entity = notificationRepository.findByNotificationId(notificationId)
                            .orElseThrow(() -> new AssertionError("Notification not found: " + notificationId));

                    assertThat(entity.getStatus()).isEqualTo(NotificationStatus.SENT);
                    assertThat(entity.getRetryCount()).isZero();
                    assertThat(entity.getErrorCode()).isNull();
                    assertThat(entity.getErrorMessage()).isNull();
                    assertThat(entity.getRecipient()).isEqualTo("user@example.com");
                });
    }
}