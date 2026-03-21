package com.example.notificationservice.messaging;

import com.example.notificationservice.config.RabbitTopologyProperties;
import com.example.notificationservice.dto.NotificationMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RoutingKeyResolver routingKeyResolver;
    private final RabbitTopologyProperties properties;

    public NotificationPublisher(
            RabbitTemplate rabbitTemplate,
            RoutingKeyResolver routingKeyResolver,
            RabbitTopologyProperties properties
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.routingKeyResolver = routingKeyResolver;
        this.properties = properties;
    }

    public void publish(NotificationMessage message) {
        String routingKey = routingKeyResolver.resolve(message.getChannel());
        rabbitTemplate.convertAndSend(properties.getExchange(), routingKey, message);
    }

    public void publishToEmailRetry(NotificationMessage message) {
        rabbitTemplate.convertAndSend("", properties.getEmailRetryQueue(), message);
    }

    public void publishToEmailDlq(NotificationMessage message) {
        rabbitTemplate.convertAndSend("", properties.getEmailDlqQueue(), message);
    }
}