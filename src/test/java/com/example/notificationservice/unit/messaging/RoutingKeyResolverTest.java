package com.example.notificationservice.unit.messaging;

import com.example.notificationservice.config.RabbitTopologyProperties;
import com.example.notificationservice.messaging.RoutingKeyResolver;
import com.example.notificationservice.model.NotificationChannel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoutingKeyResolverTest {

    @Test
    void shouldResolveEmailRoutingKey() {
        RabbitTopologyProperties properties = new RabbitTopologyProperties();
        properties.setEmailRoutingKey("notification.email");

        RoutingKeyResolver resolver = new RoutingKeyResolver(properties);

        String routingKey = resolver.resolve(NotificationChannel.EMAIL);

        assertThat(routingKey).isEqualTo("notification.email");
    }

    @Test
    void shouldThrowForUnsupportedChannel() {
        RabbitTopologyProperties properties = new RabbitTopologyProperties();
        properties.setEmailRoutingKey("notification.email");

        RoutingKeyResolver resolver = new RoutingKeyResolver(properties);

        assertThatThrownBy(() -> resolver.resolve(NotificationChannel.SMS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported channel for MVP: SMS");
    }
}
