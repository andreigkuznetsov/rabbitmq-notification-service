package com.example.notificationservice.messaging;

import com.example.notificationservice.config.RabbitTopologyProperties;
import com.example.notificationservice.model.NotificationChannel;
import org.springframework.stereotype.Component;

@Component
public class RoutingKeyResolver {

    private final RabbitTopologyProperties properties;

    public RoutingKeyResolver(RabbitTopologyProperties properties) {
        this.properties = properties;
    }

    public String resolve(NotificationChannel channel) {
        if (channel == NotificationChannel.EMAIL) {
            return properties.getEmailRoutingKey();
        }

        throw new IllegalArgumentException("Unsupported channel for MVP: " + channel);
    }
}
