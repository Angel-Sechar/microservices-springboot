
package com.ecommerce.campus.authservice.infrastructure.external.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * OpenFeign configuration for external service communication
 */
@Configuration
@EnableFeignClients(basePackages = "com.ecommerce.campus.authservice.infrastructure.external.client")
@Slf4j
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC; // NONE, BASIC, HEADERS, FULL
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Add common headers
            requestTemplate.header("User-Agent", "auth-service/1.0.0");
            requestTemplate.header("Content-Type", "application/json");
            requestTemplate.header("Accept", "application/json");

            // Add correlation ID for tracing
            String correlationId = generateCorrelationId();
            requestTemplate.header("X-Correlation-ID", correlationId);

            // Add authentication if available
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                requestTemplate.header("X-Internal-Service", "auth-service");
            }

            log.debug("Outbound request to {} with correlation ID: {}",
                    requestTemplate.url(), correlationId);
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }

    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}