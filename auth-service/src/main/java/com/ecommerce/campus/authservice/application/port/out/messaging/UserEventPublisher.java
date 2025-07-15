package com.ecommerce.campus.authservice.application.port.out.messaging;

import com.ecommerce.campus.common.application.port.out.EventPublisher;
import com.ecommerce.campus.authservice.domain.model.valueobject.Email;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;

/**
 * Port for user-specific event publishing
 * Infrastructure will implement using Kafka/RabbitMQ
 */
public interface UserEventPublisher extends EventPublisher {

    /**
     * Send welcome email to new user
     */
    void sendWelcomeEmail(UserId userId, Email email, String fullName);

    /**
     * Send email verification
     */
    void sendEmailVerification(UserId userId, Email email, String verificationToken);

    /**
     * Send password reset email
     */
    void sendPasswordResetEmail(UserId userId, Email email, String resetToken);

    /**
     * Notify about suspicious login activity
     */
    void notifySuspiciousActivity(UserId userId, Email email, String ipAddress, String userAgent);
}
