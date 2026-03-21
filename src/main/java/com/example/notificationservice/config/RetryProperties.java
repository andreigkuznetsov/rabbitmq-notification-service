package com.example.notificationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
@ConfigurationProperties(prefix = "app.retry")
public class RetryProperties {

    private int maxAttempts;
    private long emailRetryDelayMs;

}
