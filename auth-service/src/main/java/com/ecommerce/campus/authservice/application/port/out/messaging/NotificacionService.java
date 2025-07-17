package com.ecommerce.campus.authservice.application.port.out.messaging;

import com.ecommerce.campus.authservice.domain.model.valueobject.Email;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;

/**
 * Port for notification operations
 * Will be implemented by NotificationServiceAdapter using OpenFeign
 */
public interface NotificationService {

    void sendWelcomeEmail(UserId userId, Email email, String fullName);

    void sendPasswordResetEmail(UserId userId, Email email, String resetToken);

    void sendEmailVerification(UserId userId, Email email, String verificationToken);

    void notifySuspiciousActivity(UserId userId, Email email, String ipAddress, String userAgent);
}