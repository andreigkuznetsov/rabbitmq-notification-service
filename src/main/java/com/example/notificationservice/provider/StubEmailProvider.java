package com.example.notificationservice.provider;

import com.example.notificationservice.dto.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StubEmailProvider implements EmailProvider {

    private static final Logger log = LoggerFactory.getLogger(StubEmailProvider.class);

    @Override
    public void send(NotificationMessage message) {
        log.info(
                "Stub email sent. notificationId={}, recipient={}, templateCode={}",
                message.getNotificationId(),
                message.getRecipient(),
                message.getTemplateCode()
        );
    }
}