package com.example.notificationservice.integration;

import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.support.AbstractContainerBaseTest;
import com.example.notificationservice.support.client.NotificationClient;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest extends AbstractContainerBaseTest {

    @Autowired
    protected NotificationRepository notificationRepository;

    @Autowired
    protected TestRestTemplate restTemplate;

    protected NotificationClient notificationClient;

    @BeforeEach
    void baseSetUp() {
        notificationRepository.deleteAll();
        notificationClient = new NotificationClient(restTemplate);
    }
}
