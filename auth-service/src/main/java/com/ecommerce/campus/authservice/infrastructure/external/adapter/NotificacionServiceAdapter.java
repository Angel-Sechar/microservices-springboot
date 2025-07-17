package com.ecommerce.campus.authservice.infrastructure.external.adapter;

import com.ecommerce.campus.authservice.application.port.out.messaging.NotificationService;
import com.ecommerce.campus.authservice.domain.model.valueobject.Email;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;
import com.ecommerce.campus.authservice.infrastructure.external.client.NotificationFeignClient;
import com.ecommerce.campus.authservice.infrastructure.external.mapper.ExternalServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adapter for notification service using OpenFeign
 * Implements application port using external HTTP service
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceAdapter implements NotificationService {

    private final NotificationFeignClient notificationClient;
    private final ExternalServiceMapper mapper;

    @Override
    public void sendWelcomeEmail(UserId userId, Email email, String fullName) {
        try {
            var request = mapper.toWelcomeEmailRequest(userId, email, fullName);
            var response = notificationClient.sendEmail(request);

            log.info("Welcome email sent successfully to {} with messageId: {}",
                    email.getValue(), response.getMessageId());

        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", email.getValue(), e.getMessage());
            // Don't fail user registration if email fails
        }
    }

    @Override
    public void sendPasswordResetEmail(UserId userId, Email email, String resetToken) {
        try {
            var request = mapper.toPasswordResetEmailRequest(userId, email, resetToken);
            var response = notificationClient.sendEmail(request);

            log.info("Password reset email sent successfully to {} with messageId: {}",
                    email.getValue(), response.getMessageId());

        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", email.getValue(), e.getMessage());
            throw new NotificationException("Failed to send password reset email", e);
        }
    }

    @Override
    public void sendEmailVerification(UserId userId, Email email, String verificationToken) {
        try {
            var request = mapper.toEmailVerificationRequest(userId, email, verificationToken);
            var response = notificationClient.sendEmail(request);

            log.info("Email verification sent successfully to {} with messageId: {}",
                    email.getValue(), response.getMessageId());

        } catch (Exception e) {
            log.error("Failed to send email verification to {}: {}", email.getValue(), e.getMessage());
            throw new NotificationException("Failed to send email verification", e);
        }
    }
}