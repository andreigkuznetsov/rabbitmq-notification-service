package com.example.notificationservice.e2e;

import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.support.TestDataFactory;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.Map;

import static com.example.notificationservice.model.TestTemplateCodes.ORDER_CREATED;
import static org.assertj.core.api.Assertions.assertThat;

class NotificationIdempotencyE2ETest extends BaseE2ETest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void shouldNotCreateDuplicateNotificationForSameNotificationId() {
        String notificationId = "NTF-IDEMPOTENT-1";
        Map<String, Object> requestBody =
                TestDataFactory.emailRequestBody(notificationId, ORDER_CREATED);

        ResponseEntity<String> firstResponse = notificationClient.createNotification(requestBody);

        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    NotificationEntity entity = notificationRepository.findByNotificationId(notificationId)
                            .orElseThrow(() -> new AssertionError("Notification not found: " + notificationId));

                    assertThat(entity).isNotNull();
                });

        ResponseEntity<String> secondResponse = notificationClient.createNotification(requestBody);

        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(secondResponse.getBody()).contains("Notification already exists");

        long count = notificationRepository.findAll().stream()
                .filter(it -> notificationId.equals(it.getNotificationId()))
                .count();

        assertThat(count).isEqualTo(1);
    }
}