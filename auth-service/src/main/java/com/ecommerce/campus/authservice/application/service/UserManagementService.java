package com.ecommerce.campus.authservice.application.service;

import com.ecommerce.campus.authservice.application.port.out.repository.UserRepository;
import com.ecommerce.campus.authservice.application.port.out.security.PasswordEncoder;
import com.ecommerce.campus.authservice.application.port.out.messaging.UserEventPublisher;
import com.ecommerce.campus.authservice.application.dto.command.ChangePasswordCommand;
import com.ecommerce.campus.authservice.application.dto.command.ActivateUserCommand;
import com.ecommerce.campus.authservice.application.dto.response.UserResponse;
import com.ecommerce.campus.authservice.application.mapper.AuthServiceMapper;
import com.ecommerce.campus.authservice.domain.model.aggregate.User;
import com.ecommerce.campus.authservice.domain.model.valueobject.Password;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;
import com.ecommerce.campus.authservice.domain.service.AuthenticationDomainService;
import com.ecommerce.campus.authservice.domain.exception.InvalidCredentialsException;
import com.ecommerce.campus.authservice.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for user management operations
 * Handles password changes, account activation, etc.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher eventPublisher;
    private final AuthServiceMapper userMapper;
    private final AuthenticationDomainService authenticationDomainService;

    /**
     * Change user password
     */
    @Transactional
    public UserResponse changePassword(ChangePasswordCommand command) {
        log.info("Password change request for user: {}", command.getUserId());

        // 1. Find user
        UserId userId = UserId.of(command.getUserId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 2. Verify current password
        Password currentPassword = Password.ofRaw(command.getCurrentPassword());
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("Invalid current password for user: {}", userId);
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // 3. Validate password change through domain service
        Password newPassword = Password.ofRaw(command.getNewPassword());
        authenticationDomainService.validatePasswordChange(user, currentPassword, newPassword);

        // 4. Encode new password and update user
        Password encodedNewPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedNewPassword);

        // 5. Save updated user
        User updatedUser = userRepository.save(user);
        log.info("Password changed successfully for user: {}", userId);

        // 6. Publish security event (optional - password changed notification)
        try {
            eventPublisher.notifySuspiciousActivity(
                    userId,
                    user.getEmail(),
                    "system",
                    "Password changed successfully"
            );
        } catch (Exception e) {
            log.warn("Failed to publish password change event for user: {}", userId, e);
        }

        return userMapper.toResponse(updatedUser);
    }

    /**
     * Activate user account with verification token
     */
    @Transactional
    public UserResponse activateUser(ActivateUserCommand command) {
        log.info("Account activation request for user: {}", command.getUserId());

        // 1. Find user
        UserId userId = UserId.of(command.getUserId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 2. Validate verification token
        // Note: In a real implementation, you'd validate the token against stored value
        // or use JWT-based verification tokens
        if (!isValidVerificationToken(command.getVerificationToken(), user)) {
            log.warn("Invalid verification token for user: {}", userId);
            throw new InvalidCredentialsException("Invalid verification token");
        }

        // 3. Activate user account
        user.activate();

        // 4. Save activated user
        User activatedUser = userRepository.save(user);
        log.info("User account activated successfully: {}", userId);

        // 5. Send welcome email for activated user
        try {
            eventPublisher.sendWelcomeEmail(
                    userId,
                    user.getEmail(),
                    user.getFullName()
            );
        } catch (Exception e) {
            log.warn("Failed to send welcome email for activated user: {}", userId, e);
        }

        return userMapper.toResponse(activatedUser);
    }

    /**
     * Suspend user account (admin operation)
     */
    @Transactional
    public UserResponse suspendUser(String userIdStr, String reason) {
        log.info("Account suspension request for user: {}, reason: {}", userIdStr, reason);

        UserId userId = UserId.of(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.suspend();
        User suspendedUser = userRepository.save(user);

        log.warn("User account suspended: {}, reason: {}", userId, reason);
        return userMapper.toResponse(suspendedUser);
    }

    /**
     * Unlock user account (admin operation)
     */
    @Transactional
    public UserResponse unlockUser(String userIdStr) {
        log.info("Account unlock request for user: {}", userIdStr);

        UserId userId = UserId.of(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.unlock();
        User unlockedUser = userRepository.save(user);

        log.info("User account unlocked: {}", userId);
        return userMapper.toResponse(unlockedUser);
    }

    /**
     * Validate verification token
     * In a real implementation, this would check against stored tokens or validate JWT
     */
    private boolean isValidVerificationToken(String token, User user) {
        // Simplified validation - in production, use proper token validation
        return token != null &&
                token.startsWith("verification_token_") &&
                token.contains(user.getId().toString());
    }
}
