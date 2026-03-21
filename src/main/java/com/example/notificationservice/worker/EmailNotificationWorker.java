package com.example.notificationservice.worker;

import com.example.notificationservice.config.RetryProperties;
import com.example.notificationservice.dto.NotificationMessage;
import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.exception.RetryableProviderException;
import com.example.notificationservice.messaging.NotificationPublisher;
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
    private final NotificationPublisher notificationPublisher;
    private final RetryProperties retryProperties;

    public EmailNotificationWorker(
            NotificationRepository notificationRepository,
            NotificationStatusService notificationStatusService,
            EmailProvider emailProvider,
            NotificationPublisher notificationPublisher,
            RetryProperties retryProperties
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationStatusService = notificationStatusService;
        this.emailProvider = emailProvider;
        this.notificationPublisher = notificationPublisher;
        this.retryProperties = retryProperties;
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
        } catch (RetryableProviderException ex) {
            if (entity.getRetryCount() + 1 <= retryProperties.getMaxAttempts()) {
                notificationStatusService.markRetry(entity, "RETRYABLE_PROVIDER_ERROR", ex.getMessage());
                notificationPublisher.publishToEmailRetry(message);

                log.warn(
                        "Notification scheduled for retry. notificationId={}, retryCount={}, error={}",
                        message.getNotificationId(),
                        entity.getRetryCount() + 1,
                        ex.getMessage()
                );
            } else {
                notificationStatusService.markFailed(entity, "RETRY_EXHAUSTED", ex.getMessage());
                notificationPublisher.publishToEmailDlq(message);

                log.error(
                        "Retry attempts exhausted. notificationId={}, error={}",
                        message.getNotificationId(),
                        ex.getMessage()
                );
            }
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