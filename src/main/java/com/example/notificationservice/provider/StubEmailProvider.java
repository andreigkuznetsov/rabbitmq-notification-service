package com.example.notificationservice.provider;

import com.example.notificationservice.dto.NotificationMessage;
import com.example.notificationservice.exception.RetryableProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.notificationservice.model.TestTemplateCodes.FAIL_ONCE_THEN_SUCCESS;
import static com.example.notificationservice.model.TestTemplateCodes.FORCE_RETRY;

@Component
public class StubEmailProvider implements EmailProvider {

    private static final Logger log = LoggerFactory.getLogger(StubEmailProvider.class);

    private final Set<String> failOnceProcessedNotificationIds = ConcurrentHashMap.newKeySet();

    @Override
    public void send(NotificationMessage message) {
        if (FORCE_RETRY.equals(message.getTemplateCode())) {
            throw new RetryableProviderException("Temporary email provider failure");
        }

        if (FAIL_ONCE_THEN_SUCCESS.equals(message.getTemplateCode())
                && failOnceProcessedNotificationIds.add(message.getNotificationId())) {
            throw new RetryableProviderException("Temporary email provider failure on first attempt");
        }

        log.info(
                "Stub email sent. notificationId={}, recipient={}, templateCode={}",
                message.getNotificationId(),
                message.getRecipient(),
                message.getTemplateCode()
        );
    }
}