package com.example.notificationservice.e2e;

import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.repository.NotificationRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationIdempotencyE2ETest extends BaseE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void shouldNotCreateDuplicateNotificationForSameNotificationId() {
        String notificationId = "NTF-IDEMPOTENT-1";

        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", "ORD-555");
        payload.put("amount", 1200);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("notificationId", notificationId);
        requestBody.put("userId", "USR-77");
        requestBody.put("channel", "EMAIL");
        requestBody.put("recipient", "user@example.com");
        requestBody.put("templateCode", "ORDER_CREATED");
        requestBody.put("payload", payload);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> firstResponse = restTemplate.postForEntity(
                "/api/v1/notifications",
                request,
                String.class
        );

        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> {
                    NotificationEntity entity = notificationRepository.findByNotificationId(notificationId)
                            .orElseThrow(() -> new AssertionError("Notification not found: " + notificationId));

                    assertThat(entity).isNotNull();
                });

        ResponseEntity<String> secondResponse = restTemplate.postForEntity(
                "/api/v1/notifications",
                request,
                String.class
        );

        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(secondResponse.getBody()).contains("Notification already exists");

        long count = notificationRepository.findAll().stream()
                .filter(it -> notificationId.equals(it.getNotificationId()))
                .count();

        assertThat(count).isEqualTo(1);
    }
}
