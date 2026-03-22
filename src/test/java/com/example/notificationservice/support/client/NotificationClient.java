package com.example.notificationservice.support.client;

import com.example.notificationservice.support.api.NotificationApi;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

public class NotificationClient {

    private final TestRestTemplate restTemplate;

    public NotificationClient(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> createNotification(Map<String, Object> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForEntity(
                NotificationApi.NOTIFICATIONS,
                request,
                String.class
        );
    }
}
