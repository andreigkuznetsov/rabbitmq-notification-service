package com.example.notificationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
@ConfigurationProperties(prefix = "app.rabbit")
public class RabbitTopologyProperties {

    private String exchange;
    private String emailQueue;
    private String emailRoutingKey;
    private String emailRetryQueue;
    private String emailRetryRoutingKey;
    private String emailDlqQueue;
}
