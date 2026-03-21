package com.example.notificationservice.e2e;

import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationRetryRecoveryE2ETest extends BaseE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void shouldRetryOnceAndThenProcessNotificationSuccessfully() {
        String notificationId = "NTF-" + UUID.randomUUID();

        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", "ORD-555");
        payload.put("amount", 1200);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("notificationId", notificationId);
        requestBody.put("userId", "USR-77");
        requestBody.put("channel", "EMAIL");
        requestBody.put("recipient", "user@example.com");
        requestBody.put("templateCode", "FAIL_ONCE_THEN_SUCCESS");
        requestBody.put("payload", payload);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/notifications",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).contains(notificationId);
        assertThat(response.getBody()).contains("QUEUED");

        Awaitility.await()
                .atMost(Duration.ofSeconds(20))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> {
                    NotificationEntity entity = notificationRepository.findByNotificationId(notificationId)
                            .orElseThrow(() -> new AssertionError("Notification not found: " + notificationId));

                    assertThat(entity.getStatus()).isEqualTo(NotificationStatus.SENT);
                    assertThat(entity.getRetryCount()).isEqualTo(1);
                    assertThat(entity.getErrorCode()).isNull();
                    assertThat(entity.getErrorMessage()).isNull();
                    assertThat(entity.getRecipient()).isEqualTo("user@example.com");
                });
    }
}
