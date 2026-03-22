package com.example.notificationservice.unit.service;

import com.example.notificationservice.dto.CreateNotificationRequest;
import com.example.notificationservice.exception.NotificationValidationException;
import com.example.notificationservice.model.NotificationChannel;
import com.example.notificationservice.service.NotificationValidationService;
import com.example.notificationservice.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.example.notificationservice.model.TestTemplateCodes.ORDER_CREATED;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationValidationServiceTest {

    private final NotificationValidationService validationService = new NotificationValidationService();

    @Test
    void shouldAllowEmailChannel() {
        String notificationId = TestDataFactory.randomNotificationId();
        CreateNotificationRequest request =
                TestDataFactory.emailRequest(notificationId, ORDER_CREATED);

        assertThatCode(() -> validationService.validate(request))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({
            "SMS, +79991234567",
            "PUSH, device-token-1"
    })
    void shouldRejectUnsupportedChannelsInMvp(NotificationChannel channel, String recipient) {
        String notificationId = TestDataFactory.randomNotificationId();
        CreateNotificationRequest request =
                TestDataFactory.emailRequest(notificationId, ORDER_CREATED);

        request.setChannel(channel);
        request.setRecipient(recipient);

        assertThatThrownBy(() -> validationService.validate(request))
                .isInstanceOf(NotificationValidationException.class)
                .hasMessage("Only EMAIL channel is supported in MVP");
    }
}