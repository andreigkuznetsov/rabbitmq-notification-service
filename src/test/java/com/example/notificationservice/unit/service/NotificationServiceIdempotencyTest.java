package com.example.notificationservice.unit.service;

import com.example.notificationservice.dto.CreateNotificationRequest;
import com.example.notificationservice.exception.DuplicateNotificationException;
import com.example.notificationservice.messaging.NotificationPublisher;
import com.example.notificationservice.model.NotificationChannel;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.NotificationService;
import com.example.notificationservice.service.NotificationValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setNotificationId("NTF-1001");
        request.setUserId("USR-77");
        request.setChannel(NotificationChannel.EMAIL);
        request.setRecipient("user@example.com");
        request.setTemplateCode("ORDER_CREATED");
        request.setPayload(Map.of("orderId", "ORD-555"));

        when(notificationRepository.existsByNotificationId("NTF-1001")).thenReturn(true);

        assertThatThrownBy(() -> service.createNotification(request))
                .isInstanceOf(DuplicateNotificationException.class)
                .hasMessage("Notification already exists: NTF-1001");

        verify(notificationRepository, never()).save(any());
        verify(notificationPublisher, never()).publish(any());
    }
}
