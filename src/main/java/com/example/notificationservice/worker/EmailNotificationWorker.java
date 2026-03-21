package com.example.notificationservice.worker;

import com.example.notificationservice.config.RabbitTopologyProperties;
import com.example.notificationservice.dto.NotificationMessage;
import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.provider.EmailProvider;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.NotificationStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationWorker {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationWorker.class);

    private final NotificationRepository notificationRepository;
    private final NotificationStatusService notificationStatusService;
    private final EmailProvider emailProvider;

    public EmailNotificationWorker(
            NotificationRepository notificationRepository,
            NotificationStatusService notificationStatusService,
            EmailProvider emailProvider
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationStatusService = notificationStatusService;
        this.emailProvider = emailProvider;
    }

    @RabbitListener(queues = "${app.rabbit.email-queue}")
    public void handle(NotificationMessage message) {
        NotificationEntity entity = notificationRepository.findByNotificationId(message.getNotificationId())
                .orElse(null);

        if (entity == null) {
            log.warn("Notification not found for processing. notificationId={}", message.getNotificationId());
            return;
        }

        try {
            notificationStatusService.markProcessing(entity);
            emailProvider.send(message);
            notificationStatusService.markSent(entity);

            log.info(
                    "Notification processed successfully. notificationId={}, channel={}",
                    message.getNotificationId(),
                    message.getChannel()
            );
        } catch (Exception ex) {
            notificationStatusService.markFailed(entity, "EMAIL_PROVIDER_ERROR", ex.getMessage());

            log.error(
                    "Notification processing failed. notificationId={}, error={}",
                    message.getNotificationId(),
                    ex.getMessage(),
                    ex
            );
        }
    }
}