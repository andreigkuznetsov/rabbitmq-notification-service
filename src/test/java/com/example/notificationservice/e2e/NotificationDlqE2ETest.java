package com.example.notificationservice.e2e;

import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationDlqE2ETest extends BaseE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void shouldMoveNotificationToFailedStatusAndDlqAfterRetryExhaustion() {
        String notificationId = "NTF-" + UUID.randomUUID();

        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", "ORD-555");
        payload.put("amount", 1200);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("notificationId", notificationId);
        requestBody.put("userId", "USR-77");
        requestBody.put("channel", "EMAIL");
        requestBody.put("recipient", "user@example.com");
        requestBody.put("templateCode", "FORCE_RETRY");
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

        Awaitility.await()
                .atMost(Duration.ofSeconds(20))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    NotificationEntity entity = notificationRepository.findByNotificationId(notificationId)
                            .orElseThrow(() -> new AssertionError("Notification not found: " + notificationId));

                    assertThat(entity.getStatus()).isEqualTo(NotificationStatus.FAILED);
                    assertThat(entity.getRetryCount()).isEqualTo(3);
                    assertThat(entity.getErrorCode()).isEqualTo("RETRY_EXHAUSTED");
                });

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> {
                    Message dlqMessage = rabbitTemplate.receive("email.dlq.queue", 1000);

                    assertThat(dlqMessage).isNotNull();
                    assertThat(new String(dlqMessage.getBody())).contains(notificationId);
                });
    }
}