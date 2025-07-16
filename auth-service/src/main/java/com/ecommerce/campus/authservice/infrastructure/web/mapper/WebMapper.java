package com.ecommerce.campus.authservice.infrastructure.web.mapper;

import com.ecommerce.campus.authservice.application.dto.command.*;
import com.ecommerce.campus.authservice.application.dto.response.*;
import com.ecommerce.campus.authservice.infrastructure.web.dto.request.*;
import com.ecommerce.campus.authservice.infrastructure.web.dto.response.*;
import jakarta.servlet.http.HttpServletRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * MapStruct mapper for Web layer ↔ Application layer conversions
 * Handles translation between web DTOs and application DTOs
 */
@Mapper(componentModel = "spring")
@Component
public interface WebMapper {

    // ========================================
    // REQUEST MAPPINGS (Web → Application)
    // ========================================

    /**
     * Map RegisterRequest to RegisterUserCommand
     */
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "role", source = "role", defaultValue = "USER")
    RegisterUserCommand toRegisterUserCommand(RegisterRequest request);

    /**
     * Map LoginRequest to LoginCommand with HTTP request metadata
     */
    default LoginCommand toLoginCommand(LoginRequest request, HttpServletRequest httpRequest) {
        return LoginCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .rememberMe(request.getRememberMe() != null ? request.getRememberMe() : false)
                .ipAddress(extractClientIpAddress(httpRequest))
                .userAgent(httpRequest.getHeader("User-Agent"))
                .deviceInfo(request.getDeviceInfo())
                .build();
    }

    /**
     * Map TokenValidationRequest to ValidateTokenCommand
     */
    @Mapping(target = "token", source = "token")
    @Mapping(target = "includeUserDetails", source = "includeUserDetails", defaultValue = "false")
    ValidateTokenCommand toValidateTokenCommand(TokenValidationRequest request);

    /**
     * Map ChangePasswordRequest to ChangePasswordCommand
     */
    default ChangePasswordCommand toChangePasswordCommand(String userId, ChangePasswordRequest request) {
        return ChangePasswordCommand.builder()
                .userId(userId)
                .currentPassword(request.getCurrentPassword())
                .newPassword(request.getNewPassword())
                .build();
    }

    /**
     * Map UpdateProfileRequest to UpdateUserProfileCommand
     */
    default UpdateUserProfileCommand toUpdateUserProfileCommand(String userId, UpdateProfileRequest request) {
        return UpdateUserProfileCommand.builder()
                .userId(userId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
    }

    /**
     * Create GetUserProfileCommand from userId
     */
    default GetUserProfileCommand toGetUserProfileCommand(String userId) {
        return GetUserProfileCommand.builder()
                .userId(userId)
                .build();
    }

    // ========================================
    // RESPONSE MAPPINGS (Application → Web)
    // ========================================

    /**
     * Map UserResponse (application) to UserResponse (web)
     */
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "canLogin", source = "canLogin")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "lastLoginAt", source = "lastLoginAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    com.ecommerce.campus.authservice.infrastructure.web.dto.response.UserResponse
    toWebUserResponse(com.ecommerce.campus.authservice.application.dto.response.UserResponse applicationResponse);

    /**
     * Map AuthenticationResponse (application) to AuthResponse (web)
     */
    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    @Mapping(target = "tokenType", source = "tokenType")
    @Mapping(target = "expiresIn", source = "expiresIn")
    @Mapping(target = "expiresAt", source = "expiresAt")
    @Mapping(target = "user", source = "user", qualifiedByName = "mapUserResponseToWeb")
    @Mapping(target = "sessionInfo", source = ".", qualifiedByName = "mapToSessionInfo")
    AuthResponse toWebAuthResponse(AuthenticationResponse applicationResponse);

    /**
     * Map TokenValidationResponse (application) to TokenValidationResponse (web)
     */
    @Mapping(target = "valid", source = "valid")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "authorities", source = "authorities")
    @Mapping(target = "expiresAt", source = "expiresAt")
    @Mapping(target = "expired", source = "expired")
    @Mapping(target = "userDetails", source = "userDetails", qualifiedByName = "mapUserResponseToWeb")
    @Mapping(target = "errorCode", source = "errorCode")
    @Mapping(target = "errorMessage", source = "errorMessage")
    com.ecommerce.campus.authservice.infrastructure.web.dto.response.TokenValidationResponse
    toWebTokenValidationResponse(com.ecommerce.campus.authservice.application.dto.response.TokenValidationResponse applicationResponse);

    // ========================================
    // HELPER METHODS
    // ========================================

    /**
     * Helper method to map UserResponse for nested mappings
     */
    @Named("mapUserResponseToWeb")
    default com.ecommerce.campus.authservice.infrastructure.web.dto.response.UserResponse
    mapUserResponseToWeb(com.ecommerce.campus.authservice.application.dto.response.UserResponse user) {
        if (user == null) return null;
        return toWebUserResponse(user);
    }

    /**
     * Helper method to create SessionInfo from AuthenticationResponse
     */
    @Named("mapToSessionInfo")
    default AuthResponse.SessionInfo mapToSessionInfo(AuthenticationResponse response) {
        return AuthResponse.SessionInfo.builder()
                .loginTime(LocalDateTime.now())
                .ipAddress(response.getIpAddress())
                .userAgent(response.getUserAgent())
                .rememberMe(response.isRememberMe())
                .build();
    }

    /**
     * Extract client IP address from HTTP request
     */
    default String extractClientIpAddress(HttpServletRequest request) {
        // Check X-Forwarded-For header (common in load balancers/proxies)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return xForwardedFor.split(",")[0].trim();
        }

        // Check X-Real-IP header (used by Nginx)
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        // Check X-Forwarded header
        String xForwarded = request.getHeader("X-Forwarded");
        if (xForwarded != null && !xForwarded.isEmpty() && !"unknown".equalsIgnoreCase(xForwarded)) {
            return xForwarded;
        }

        // Check Forwarded-For header
        String forwardedFor = request.getHeader("Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(forwardedFor)) {
            return forwardedFor;
        }

        // Check Forwarded header
        String forwarded = request.getHeader("Forwarded");
        if (forwarded != null && !forwarded.isEmpty() && !"unknown".equalsIgnoreCase(forwarded)) {
            return forwarded;
        }

        // Fall back to remote address
        return request.getRemoteAddr();
    }

    /**
     * Calculate token expiration in seconds from LocalDateTime
     */
    default Long calculateExpiresIn(LocalDateTime expiresAt) {
        if (expiresAt == null) return null;
        return Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
    }
}