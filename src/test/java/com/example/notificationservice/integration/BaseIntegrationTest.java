package com.example.notificationservice.integration;

import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.support.AbstractContainerBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest extends AbstractContainerBaseTest {

    @Autowired
    protected NotificationRepository notificationRepository;

    @BeforeEach
    void cleanDatabase() {
        notificationRepository.deleteAll();
    }
}
