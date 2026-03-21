package com.example.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RabbitTopologyProperties.class, RetryProperties.class})
public class RabbitConfig {

    @Bean
    public TopicExchange notificationExchange(RabbitTopologyProperties properties) {
        return new TopicExchange(properties.getExchange(), true, false);
    }

    @Bean
    public Queue emailQueue(RabbitTopologyProperties properties) {
        return new Queue(properties.getEmailQueue(), true);
    }

    @Bean
    public Binding emailBinding(
            Queue emailQueue,
            TopicExchange notificationExchange,
            RabbitTopologyProperties properties
    ) {
        return BindingBuilder
                .bind(emailQueue)
                .to(notificationExchange)
                .with(properties.getEmailRoutingKey());
    }

    @Bean
    public Queue emailRetryQueue(RabbitTopologyProperties properties, RetryProperties retryProperties) {
        return QueueBuilder.durable(properties.getEmailRetryQueue())
                .withArgument("x-message-ttl", retryProperties.getEmailRetryDelayMs())
                .withArgument("x-dead-letter-exchange", properties.getExchange())
                .withArgument("x-dead-letter-routing-key", properties.getEmailRoutingKey())
                .build();
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
