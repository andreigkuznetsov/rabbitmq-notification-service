package com.example.notificationservice.e2e;

import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.model.NotificationErrorCode;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.support.TestDataFactory;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.Map;

import static com.example.notificationservice.model.TestTemplateCodes.FORCE_RETRY;
import static org.assertj.core.api.Assertions.assertThat;

class NotificationRetryExhaustedE2ETest extends BaseE2ETest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void shouldFailAfterRetryAttemptsAreExhausted() {
        String notificationId = TestDataFactory.randomNotificationId();
        Map<String, Object> requestBody =
                TestDataFactory.emailRequestBody(notificationId, FORCE_RETRY);

        ResponseEntity<String> response = notificationClient.createNotification(requestBody);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).contains(notificationId);
        assertThat(response.getBody()).contains(NotificationStatus.QUEUED.name());

        Awaitility.await()
                .atMost(Duration.ofSeconds(20))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> {
                    NotificationEntity entity = notificationRepository.findByNotificationId(notificationId)
                            .orElseThrow(() -> new AssertionError("Notification not found: " + notificationId));

                    assertThat(entity.getStatus()).isEqualTo(NotificationStatus.FAILED);
                    assertThat(entity.getRetryCount()).isEqualTo(3);
                    assertThat(entity.getErrorCode()).isEqualTo(NotificationErrorCode.RETRY_EXHAUSTED.name());
                    assertThat(entity.getErrorMessage()).contains("Temporary email provider failure");
                    assertThat(entity.getRecipient()).isEqualTo("user@example.com");
                });
    }
}