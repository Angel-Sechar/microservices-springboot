package com.ecommerce.campus.authservice.application.service;

import com.ecommerce.campus.authservice.application.port.in.usecase.RegisterUserUseCase;
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
    private final UserEventPublisher eventPublisher;
    private final AuthServiceMapper userMapper;
    private final AuthenticationDomainService authenticationDomainService;

    @Override
    @Transactional
    public UserResponse execute(RegisterUserCommand command) {
        log.info("Starting user registration for email: {}", command.getEmail());

        // 1. Convert to domain value objects and validate
        Email email = Email.of(command.getEmail());

        // 2. Business rule validation through domain service
        authenticationDomainService.validateUserRegistration(
                email,
                com.ecommerce.campus.auth.domain.model.valueobject.Password.ofRaw(command.getPassword())
        );

        // 3. Check if user already exists
        if (userRepository.existsByEmail(email)) {
            log.warn("Registration attempt for existing email: {}", email);
            throw new UserAlreadyExistsException(email);
        }

        // 4. Create user aggregate through domain factory
        User user = userMapper.toDomain(command);

        // 5. Encode password before saving
        user.changePassword(passwordEncoder.encode(user.getPassword()));

        // 6. Save user to repository
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // 7. Publish domain events (welcome email, etc.)
        publishUserEvents(savedUser);

        // 8. Convert to response DTO
        return userMapper.toResponse(savedUser);
    }

    private void publishUserEvents(User user) {
        try {
            // Send welcome email
            eventPublisher.sendWelcomeEmail(
                    user.getId(),
                    user.getEmail(),
                    user.getFullName()
            );

            // Send email verification if needed
            if (user.getStatus().requiresVerification()) {
                eventPublisher.sendEmailVerification(
                        user.getId(),
                        user.getEmail(),
                        generateVerificationToken(user)
                );
            }

            log.info("User registration events published for user: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to publish user registration events for user: {}", user.getId(), e);
            // Don't fail the registration if event publishing fails
        }
    }

    private String generateVerificationToken(User user) {
        // This would typically generate a secure token for email verification
        // For now, return a placeholder - will be implemented in infrastructure
        return "verification_token_" + user.getId().toString();
    }
}
