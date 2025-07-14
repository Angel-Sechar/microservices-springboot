package com.ecommerce.campus.authservice.domain.service;

import com.ecommerce.campus.authservice.domain.model.aggregate.User;
import com.ecommerce.campus.authservice.domain.model.valueobject.*;
import com.ecommerce.campus.authservice.domain.exception.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * Domain service for authentication business logic
 * Contains complex business rules that don't belong to a single aggregate
 */
@Slf4j
public class AuthenticationDomainService {

    /**
     * Validate user credentials and authentication state
     */
    public void validateUserForLogin(User user, Password rawPassword) {
        log.debug("Validating user {} for login", user.getEmail());

        // Check if user exists and is not null (should not happen, but defensive)
        if (user == null) {
            throw new InvalidCredentialsException();
        }

        // Check account status first
        if (!user.getStatus().canLogin()) {
            handleAccountStatusIssues(user);
        }

        // Check if account is temporarily locked
        if (user.isTemporarilyLocked()) {
            throw new AccountLockedException(user.getLockedUntil());
        }

        // At this point, account status allows login
        log.debug("User {} passed status validation", user.getEmail());
    }

    /**
     * Handle account status issues with specific exceptions
     */
    private void handleAccountStatusIssues(User user) {
        switch (user.getStatus().getCode()) {
            case "PENDING_VERIFICATION" -> throw new AccountNotActivatedException();
            case "SUSPENDED" -> throw new InvalidCredentialsException("Account is suspended");
            case "LOCKED" -> throw new AccountLockedException();
            case "INACTIVE" -> throw new InvalidCredentialsException("Account is inactive");
            default -> throw new InvalidCredentialsException("Account cannot be used for login");
        }
    }

    /**
     * Determine appropriate user role for new registrations - pending changes
     */
    public UserRole determineInitialRole(Email email) {
        // Business rule: Admin emails
        if (isAdminEmail(email)) {
            log.info("Assigning ADMIN role to email: {}", email);
            return UserRole.ADMIN;
        }

        // Business rule: Moderator emails (if from company domain)
        if (isCompanyEmail(email)) {
            log.info("Assigning MODERATOR role to company email: {}", email);
            return UserRole.MODERATOR;
        }

        // Default role for regular users
        return UserRole.USER;
    }

    /**
     * Check if email should have admin privileges - pending changes
     */
    private boolean isAdminEmail(Email email) {
        // Business rule: These emails get admin access
        return email.getValue().equals("admin@campus.com") ||
                email.getValue().equals("admin@ecommerce.campus") ||
                email.getValue().endsWith("@admin.campus.com");
    }

    /**
     * Check if email is from company domain - disable - pending changes
     */
    private boolean isCompanyEmail(Email email) {
        String domain = email.getDomain();
        return domain.equals("campus.com") ||
                domain.equals("ecommerce.campus") ||
                domain.endsWith(".campus.com");
    }

    /**
     * Validate password change requirements
     */
    public void validatePasswordChange(User user, Password currentPassword, Password newPassword) {
        // Business rule: User must be active to change password
        if (!user.canLogin()) {
            throw new InvalidCredentialsException("Cannot change password for inactive account");
        }

        // Business rule: New password cannot be the same as current
        // Note: This is a simplified check - in real implementation,
        // you'd compare hashed values through the password encoder
        if (newPassword.getValue().equals(currentPassword.getValue())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        // Business rule: Password strength requirements are already enforced
        // by the Password value object validation

        log.info("Password change validation passed for user: {}", user.getEmail());
    }

    /**
     * Calculate account lockout duration based on failed attempts
     */
    public LocalDateTime calculateLockoutEndTime(int failedAttempts) {
        return switch (failedAttempts) {
            case 5, 6, 7 -> LocalDateTime.now().plusMinutes(30);  // 30 minutes
            case 8, 9 -> LocalDateTime.now().plusHours(2);       // 2 hours
            case 10, 11 -> LocalDateTime.now().plusHours(24);    // 24 hours
            default -> LocalDateTime.now().plusDays(7);          // 7 days for severe cases
        };
    }

    /**
     * Check if user registration is allowed
     */
    public void validateUserRegistration(Email email, Password password) {
        // Business rule: Block certain email domains
        if (isBlockedEmailDomain(email)) {
            throw new IllegalArgumentException("Registration not allowed for this email domain");
        }

        // Business rule: Password strength is already validated by Password value object

        // Business rule: Rate limiting could be added here
        // (e.g., check if too many registrations from same IP)

        log.info("User registration validation passed for email: {}", email);
    }

    /**
     * Check if email domain is blocked -temp
     */
    private boolean isBlockedEmailDomain(Email email) {
        String domain = email.getDomain().toLowerCase();
        return domain.equals("tempmail.org") ||
                domain.equals("10minutemail.com") ||
                domain.equals("guerrillamail.com") ||
                domain.contains("temp") ||
                domain.contains("disposable");
    }
}