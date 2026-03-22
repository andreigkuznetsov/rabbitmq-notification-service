package com.example.notificationservice.unit.service;

import com.example.notificationservice.dto.CreateNotificationRequest;
import com.example.notificationservice.exception.DuplicateNotificationException;
import com.example.notificationservice.messaging.NotificationPublisher;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.NotificationService;
import com.example.notificationservice.service.NotificationValidationService;
import com.example.notificationservice.support.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static com.example.notificationservice.model.TestTemplateCodes.ORDER_CREATED;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class NotificationServiceIdempotencyTest {

    @Test
    void shouldRejectDuplicateNotificationId() {
        NotificationValidationService validationService = new NotificationValidationService();
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        NotificationPublisher notificationPublisher = mock(NotificationPublisher.class);
        ObjectMapper objectMapper = new ObjectMapper();

        NotificationService service = new NotificationService(
                validationService,
                notificationRepository,
                notificationPublisher,
                objectMapper
        );

        String notificationId = TestDataFactory.randomNotificationId();

        CreateNotificationRequest request =
                TestDataFactory.emailRequest(notificationId, ORDER_CREATED);

        when(notificationRepository.existsByNotificationId(notificationId)).thenReturn(true);

        assertThatThrownBy(() -> service.createNotification(request))
                .isInstanceOf(DuplicateNotificationException.class)
                .hasMessageContaining(notificationId);

        verify(notificationRepository, never()).save(any());
        verify(notificationPublisher, never()).publish(any());
    }
}