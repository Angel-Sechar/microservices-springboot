package com.ecommerce.campus.authservice.infrastructure.messaging;

import com.ecommerce.campus.authservice.application.port.out.messaging.UserEventPublisher;
import com.ecommerce.campus.authservice.domain.model.valueobject.Email;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;
import com.ecommerce.campus.common.domain.model.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Spring Events implementation of UserEventPublisher port
 * In production, this would integrate with Kafka, RabbitMQ, etc.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringUserEventPublisher implements UserEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(DomainEvent event) {
        log.debug("Publishing domain event: {}", event.getEventType());
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publishAll(Iterable<DomainEvent> events) {
        events.forEach(this::publish);
    }

    @Override
    public void sendWelcomeEmail(UserId userId, Email email, String fullName) {
        log.info("Sending welcome email to user: {} ({})", fullName, email);

        WelcomeEmailEvent event = new WelcomeEmailEvent(userId, email, fullName);
        eventPublisher.publishEvent(event);
    }

    @Override
    public void sendEmailVerification(UserId userId, Email email, String verificationToken) {
        log.info("Sending email verification to user: {}", email);

        EmailVerificationEvent event = new EmailVerificationEvent(userId, email, verificationToken);
        eventPublisher.publishEvent(event);
    }

    @Override
    public void sendPasswordResetEmail(UserId userId, Email email, String resetToken) {
        log.info("Sending password reset email to user: {}", email);

        PasswordResetEvent event = new PasswordResetEvent(userId, email, resetToken);
        eventPublisher.publishEvent(event);
    }

    @Override
    public void notifySuspiciousActivity(UserId userId, Email email, String ipAddress, String userAgent) {
        log.warn("Suspicious activity detected for user: {} from IP: {}", email, ipAddress);

        SuspiciousActivityEvent event = new SuspiciousActivityEvent(userId, email, ipAddress, userAgent);
        eventPublisher.publishEvent(event);
    }

    // Event classes for different notification types

    public record WelcomeEmailEvent(UserId userId, Email email, String fullName) {}
    public record EmailVerificationEvent(UserId userId, Email email, String verificationToken) {}
    public record PasswordResetEvent(UserId userId, Email email, String resetToken) {}
    public record SuspiciousActivityEvent(UserId userId, Email email, String ipAddress, String userAgent) {}
}
