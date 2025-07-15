package com.ecommerce.campus.authservice.domain.model.aggregate;

import com.ecommerce.campus.common.domain.model.AggregateRoot;
import com.ecommerce.campus.authservice.domain.model.valueobject.*;
import com.ecommerce.campus.authservice.domain.model.event.UserRegisteredEvent;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * User Aggregate Root - Central entity for authentication domain
 * Encapsulates all user-related business logic and invariants
 */
@Getter
public class User extends AggregateRoot<UserId> {

    private Email email;
    private Password password;
    private UserRole role;
    private UserStatus status;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    private int failedLoginAttempts;
    private LocalDateTime lockedUntil;

    // Private constructor for JPA
    protected User() {
        super();
    }

    // Private constructor for aggregate creation
    private User(UserId id, Email email, Password password, UserRole role,
                 String firstName, String lastName) {
        super(id);
        this.email = email;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = UserStatus.PENDING_VERIFICATION;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.failedLoginAttempts = 0;

        validate();
    }

    /**
     * Factory method to create a new user
     */
    public static User create(Email email, Password password, UserRole role,
                              String firstName, String lastName) {
        UserId userId = UserId.generate();
        User user = new User(userId, email, password, role, firstName, lastName);

        // Fire domain event
        user.addDomainEvent(new UserRegisteredEvent(userId, email, role));

        return user;
    }

    /**
     * Activate user account (after email verification)
     */
    public void activate() {
        if (!status.requiresVerification()) {
            throw new IllegalStateException("User is not in pending verification status");
        }

        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
    }

    /**
     * Record successful login
     */
    public void recordSuccessfulLogin() {
        if (!canLogin()) {
            throw new IllegalStateException("User cannot login in current status: " + status);
        }

        this.lastLoginAt = LocalDateTime.now();
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Record failed login attempt
     */
    public void recordFailedLoginAttempt() {
        this.failedLoginAttempts++;
        this.updatedAt = LocalDateTime.now();

        // Lock account after 5 failed attempts
        if (this.failedLoginAttempts >= 5) {
            this.status = UserStatus.LOCKED;
            this.lockedUntil = LocalDateTime.now().plusMinutes(30); // Lock for 30 minutes
        }
    }

    /**
     * ChangePasswordCommand user password
     */
    public void changePassword(Password newPassword) {
        if (!canLogin()) {
            throw new IllegalStateException("Cannot change password for user with status: " + status);
        }

        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
        this.failedLoginAttempts = 0; // Reset failed attempts on password change
    }

    /**
     * Update user profile
     */
    public void updateProfile(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }

        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Suspend user account
     */
    public void suspend() {
        if (status.equals(UserStatus.SUSPENDED)) {
            throw new IllegalStateException("User is already suspended");
        }

        this.status = UserStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Unlock user account (admin action)
     */
    public void unlock() {
        this.status = UserStatus.ACTIVE;
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if user can login
     */
    public boolean canLogin() {
        if (status.isBlocked()) {
            return false;
        }

        if (status.equals(UserStatus.LOCKED) && lockedUntil != null) {
            // Check if lock period has expired
            if (LocalDateTime.now().isAfter(lockedUntil)) {
                // Auto-unlock
                this.status = UserStatus.ACTIVE;
                this.failedLoginAttempts = 0;
                this.lockedUntil = null;
                return true;
            }
            return false;
        }

        return status.canLogin();
    }

    /**
     * Get full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Check if account is locked temporarily
     */
    public boolean isTemporarilyLocked() {
        return status.equals(UserStatus.LOCKED) &&
                lockedUntil != null &&
                LocalDateTime.now().isBefore(lockedUntil);
    }

    @Override
    public boolean isValid() {
        return email != null &&
                password != null &&
                role != null &&
                status != null &&
                firstName != null && !firstName.isBlank() &&
                lastName != null && !lastName.isBlank() &&
                createdAt != null;
    }

    @Override
    public void validate() {
        if (!isValid()) {
            throw new IllegalStateException("User aggregate is in invalid state");
        }
    }
}