package com.example.notificationservice.unit.service;

import com.example.notificationservice.dto.CreateNotificationRequest;
import com.example.notificationservice.exception.NotificationValidationException;
import com.example.notificationservice.model.NotificationChannel;
import com.example.notificationservice.service.NotificationValidationService;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationValidationServiceTest {

    private final NotificationValidationService validationService = new NotificationValidationService();

    @Test
    void shouldAllowEmailChannel() {
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setNotificationId("NTF-1001");
        request.setUserId("USR-77");
        request.setChannel(NotificationChannel.EMAIL);
        request.setRecipient("user@example.com");
        request.setTemplateCode("ORDER_CREATED");
        request.setPayload(Map.of("orderId", "ORD-1"));

        assertThatCode(() -> validationService.validate(request))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldRejectSmsChannelInMvp() {
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setNotificationId("NTF-1002");
        request.setUserId("USR-77");
        request.setChannel(NotificationChannel.SMS);
        request.setRecipient("+79991234567");
        request.setTemplateCode("ORDER_CREATED");
        request.setPayload(Map.of("orderId", "ORD-2"));

        assertThatThrownBy(() -> validationService.validate(request))
                .isInstanceOf(NotificationValidationException.class)
                .hasMessage("Only EMAIL channel is supported in MVP");
    }

    @Test
    void shouldRejectPushChannelInMvp() {
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setNotificationId("NTF-1003");
        request.setUserId("USR-77");
        request.setChannel(NotificationChannel.PUSH);
        request.setRecipient("device-token-1");
        request.setTemplateCode("ORDER_CREATED");
        request.setPayload(Map.of("orderId", "ORD-3"));

        assertThatThrownBy(() -> validationService.validate(request))
                .isInstanceOf(NotificationValidationException.class)
                .hasMessage("Only EMAIL channel is supported in MVP");
    }
}
