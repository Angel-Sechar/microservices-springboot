package com.ecommerce.campus.authservice.infrastructure.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import com.ecommerce.campus.authservice.application.port.out.messaging.UserEventPublisher;
import com.ecommerce.campus.authservice.domain.model.valueobject.Email;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;
import com.ecommerce.campus.common.domain.model.DomainEvent;

/**
 * Test configuration that provides mock implementations
 * for integration tests
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    /**
     * Mock event publisher for tests
     */
    @Bean
    @Primary
    public UserEventPublisher mockEventPublisher() {
        return new UserEventPublisher() {
            @Override
            public void publish(DomainEvent event) {
                // No-op for tests
            }

            @Override
            public void publishAll(Iterable<DomainEvent> events) {
                // No-op for tests
            }

            @Override
            public void sendWelcomeEmail(UserId userId, Email email, String fullName) {
                // No-op for tests
            }

            @Override
            public void sendEmailVerification(UserId userId, Email email, String verificationToken) {
                // No-op for tests
            }

            @Override
            public void sendPasswordResetEmail(UserId userId, Email email, String resetToken) {
                // No-op for tests
            }

            @Override
            public void notifySuspiciousActivity(UserId userId, Email email, String ipAddress, String userAgent) {
                // No-op for tests
            }
        };
    }
}