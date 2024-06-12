package ru.salfa.messenger.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "otp")
@Data
public class OtpConfig {
    private Integer length;
    private Long expiration;
    private Integer numberOfAttempts;
    private Long intervalForBlocking;
    private Long durationOfBlocking;
}