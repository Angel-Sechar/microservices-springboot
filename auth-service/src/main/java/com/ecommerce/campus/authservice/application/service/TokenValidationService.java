package com.ecommerce.campus.authservice.application.service;

import com.ecommerce.campus.authservice.application.port.in.usecase.ValidateTokenUseCase;
import com.ecommerce.campus.authservice.application.port.out.repository.UserRepository;
import com.ecommerce.campus.authservice.application.port.out.security.TokenGenerator;
import com.ecommerce.campus.authservice.application.dto.command.ValidateTokenCommand;
import com.ecommerce.campus.authservice.application.dto.response.TokenValidationResponse;
import com.ecommerce.campus.authservice.application.dto.response.UserResponse;
import com.ecommerce.campus.authservice.application.mapper.AuthServiceMapper;
import com.ecommerce.campus.authservice.domain.model.aggregate.User;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * Application service for JWT token validation
 * Used by API Gateway and other microservices
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenValidationService implements ValidateTokenUseCase {

    private final TokenGenerator tokenGenerator;
    private final UserRepository userRepository;
    private final AuthServiceMapper userMapper;

    @Override
    public TokenValidationResponse execute(ValidateTokenCommand command) {
        try {
            // 1. Basic token validation
            if (tokenGenerator.isTokenExpired(command.getToken())) {
                log.debug("Token validation failed: token expired");
                return TokenValidationResponse.invalid("EXPIRED", "Token has expired");
            }

            // 2. Extract user ID from token
            UserId userId = tokenGenerator.validateTokenAndGetUserId(command.getToken());
            if (userId == null) {
                log.debug("Token validation failed: invalid token format");
                return TokenValidationResponse.invalid("INVALID", "Invalid token format");
            }

            // 3. Check if user still exists and is active
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                log.warn("Token validation failed: user not found for ID: {}", userId);
                return TokenValidationResponse.invalid("USER_NOT_FOUND", "User no longer exists");
            }

            // 4. Check if user can still login (account status)
            if (!user.canLogin()) {
                log.warn("Token validation failed: user cannot login, status: {}", user.getStatus());
                return TokenValidationResponse.invalid("ACCOUNT_DISABLED", "User account is disabled");
            }

            // 5. Extract additional claims
            Map<String, Object> claims = tokenGenerator.extractClaims(command.getToken());
            long expirationTime = tokenGenerator.getTokenExpirationTime(command.getToken());
            LocalDateTime expiresAt = LocalDateTime.ofEpochSecond(expirationTime, 0, ZoneOffset.UTC);

            // 6. Build successful response
            TokenValidationResponse.TokenValidationResponseBuilder responseBuilder =
                    TokenValidationResponse.builder()
                            .valid(true)
                            .userId(userId.toString())
                            .email(user.getEmail().getValue())
                            .role(user.getRole().getCode())
                            .authorities(new String[]{user.getRole().getCode()})
                            .expiresAt(expiresAt)
                            .expired(false);

            // 7. Include full user details if requested
            if (command.isIncludeUserDetails()) {
                UserResponse userDetails = userMapper.toResponse(user);
                responseBuilder.userDetails(userDetails);
            }

            log.debug("Token validation successful for user: {}", user.getEmail());
            return responseBuilder.build();

        } catch (Exception e) {
            log.error("Token validation error", e);
            return TokenValidationResponse.invalid("VALIDATION_ERROR", "Token validation failed");
        }
    }
}