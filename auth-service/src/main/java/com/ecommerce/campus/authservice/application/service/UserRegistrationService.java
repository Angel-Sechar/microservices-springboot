package com.ecommerce.campus.authservice.application.service;

import com.ecommerce.campus.authservice.application.port.in.usecase.RegisterUserUseCase;
import com.ecommerce.campus.authservice.application.port.out.external.UserProfileService;
import com.ecommerce.campus.authservice.application.port.out.messaging.NotificationService;
import com.ecommerce.campus.authservice.application.port.out.repository.UserRepository;
import com.ecommerce.campus.authservice.application.port.out.security.PasswordEncoder;
import com.ecommerce.campus.authservice.application.port.out.messaging.UserEventPublisher;
import com.ecommerce.campus.authservice.application.dto.command.RegisterUserCommand;
import com.ecommerce.campus.authservice.application.dto.response.UserResponse;
import com.ecommerce.campus.authservice.application.mapper.AuthServiceMapper;
import com.ecommerce.campus.authservice.domain.model.aggregate.User;
import com.ecommerce.campus.authservice.domain.model.valueobject.Email;
import com.ecommerce.campus.authservice.domain.service.AuthenticationDomainService;
import com.ecommerce.campus.authservice.domain.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

/**
 * Application service for user registration
 * Orchestrates domain logic with infrastructure concerns
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegistrationService implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;      // ← OpenFeign adapter
    private final UserProfileService userProfileService;       // ← OpenFeign adapter
    private final AuthServiceMapper userMapper;
    private final AuthenticationDomainService authenticationDomainService;

    @Override
    @Transactional
    public UserResponse execute(RegisterUserCommand command) {
        log.info("Starting user registration for email: {}", command.getEmail());

        // 1-5. Same as before (domain logic)
        Email email = Email.of(command.getEmail());
        // ... validation, user creation, password encoding, saving

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // 6. Create profile in external service (async)
        CompletableFuture.runAsync(() -> {
            try {
                userProfileService.createUserProfile(
                        savedUser.getId(),
                        savedUser.getFirstName(),
                        savedUser.getLastName(),
                        savedUser.getEmail().getValue()
                );
            } catch (Exception e) {
                log.error("Failed to create user profile for {}: {}", savedUser.getId(), e.getMessage());
                // Handle profile creation failure
            }
        });

        // 7. Send welcome email (async)
        CompletableFuture.runAsync(() -> {
            try {
                notificationService.sendWelcomeEmail(
                        savedUser.getId(),
                        savedUser.getEmail(),
                        savedUser.getFullName()
                );
            } catch (Exception e) {
                log.error("Failed to send welcome email for {}: {}", savedUser.getId(), e.getMessage());
                // Handle email failure
            }
        });

        return userMapper.toResponse(savedUser);
    }
}