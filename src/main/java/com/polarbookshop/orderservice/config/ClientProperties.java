package com.polarbookshop.orderservice.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;

@ConfigurationProperties(prefix = "polar")
public record ClientProperties(
        @NotNull URI catalogServiceUri,
        @NotNull Duration timeout,
        @NotNull Long retryBackoffMaxAttempts,
        @NotNull Duration retryMinBackoff) {
}