package com.ecommerce.campus.authservice.application.service;

import com.ecommerce.campus.authservice.application.port.in.usecase.LoginUserUseCase;
import com.ecommerce.campus.authservice.application.port.out.repository.UserRepository;
import com.ecommerce.campus.authservice.application.port.out.security.PasswordEncoder;
import com.ecommerce.campus.authservice.application.port.out.security.TokenGenerator;
import com.ecommerce.campus.authservice.application.port.out.messaging.UserEventPublisher;
import com.ecommerce.campus.authservice.application.dto.command.LoginCommand;
import com.ecommerce.campus.authservice.application.dto.response.AuthenticationResponse;
import com.ecommerce.campus.authservice.application.dto.response.UserResponse;
import com.ecommerce.campus.authservice.application.mapper.AuthServiceMapper;
import com.ecommerce.campus.authservice.domain.model.aggregate.User;
import com.ecommerce.campus.authservice.domain.model.valueobject.Email;
import com.ecommerce.campus.authservice.domain.model.valueobject.Password;
import com.ecommerce.campus.authservice.domain.service.AuthenticationDomainService;
import com.ecommerce.campus.authservice.domain.exception.InvalidCredentialsException;
import com.ecommerce.campus.authservice.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Application service for user authentication
 * Handles login flow with security validations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthenticationService implements LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;
    private final UserEventPublisher eventPublisher;
    private final AuthServiceMapper userMapper;
    private final AuthenticationDomainService authenticationDomainService;

    @Override
    @Transactional
    public AuthenticationResponse execute(LoginCommand command) {
        log.info("Authentication attempt for email: {}", command.getEmail());

        try {
            // 1. Find user by email
            Email email = Email.of(command.getEmail());
            User user = userRepository.findByEmailForAuthentication(email)
                    .orElseThrow(() -> new UserNotFoundException(email));

            // 2. Validate user status and account state
            Password rawPassword = Password.ofRaw(command.getPassword());
            authenticationDomainService.validateUserForLogin(user, rawPassword);

            // 3. Verify password
            if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                handleFailedLogin(user, command);
                throw new InvalidCredentialsException();
            }

            // 4. Successful authentication - update user state
            user.recordSuccessfulLogin();
            User updatedUser = userRepository.save(user);

            // 5. Generate tokens
            AuthenticationResponse response = generateAuthenticationResponse(updatedUser, command);

            // 6. Log successful authentication
            log.info("Successful authentication for user: {} from IP: {}",
                    user.getEmail(), command.getIpAddress());

            return response;

        } catch (InvalidCredentialsException | UserNotFoundException e) {
            log.warn("Failed authentication attempt for email: {} from IP: {}",
                    command.getEmail(), command.getIpAddress());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during authentication for email: {}",
                    command.getEmail(), e);
            throw new InvalidCredentialsException("Authentication failed");
        }
    }

    private void handleFailedLogin(User user, LoginCommand command) {
        try {
            // Record failed attempt
            user.recordFailedLoginAttempt();
            userRepository.save(user);

            // Check if account was locked due to failed attempts
            if (user.isTemporarilyLocked()) {
                log.warn("Account locked for user: {} after failed login attempts", user.getEmail());

                // Notify user about suspicious activity
                eventPublisher.notifySuspiciousActivity(
                        user.getId(),
                        user.getEmail(),
                        command.getIpAddress(),
                        command.getUserAgent()
                );
            }

        } catch (Exception e) {
            log.error("Error handling failed login for user: {}", user.getEmail(), e);
            // Don't fail the authentication process if we can't record the failure
        }
    }

    private AuthenticationResponse generateAuthenticationResponse(User user, LoginCommand command) {
        // Determine token expiration based on remember me
        Duration accessTokenExpiration = command.isRememberMe() ?
                Duration.ofDays(30) : Duration.ofHours(24);

        // Generate tokens
        String accessToken = tokenGenerator.generateAccessToken(user, accessTokenExpiration);
        String refreshToken = tokenGenerator.generateRefreshToken(user);

        // Calculate expiration details
        long expiresIn = accessTokenExpiration.getSeconds();
        LocalDateTime expiresAt = LocalDateTime.now().plus(accessTokenExpiration);

        // Map user to response DTO
        UserResponse userResponse = userMapper.toResponse(user);

        // Build authentication response
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .expiresAt(expiresAt)
                .user(userResponse)
                .authorities(new String[]{user.getRole().getCode()})
                .requiresPasswordChange(false) // Could add logic for password expiration
                .isFirstLogin(user.getLastLoginAt() == null)
                .build();
    }
}
