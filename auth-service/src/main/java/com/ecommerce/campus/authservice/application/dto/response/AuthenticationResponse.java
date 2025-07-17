package com.ecommerce.campus.authservice.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;        // seconds until expiration
    private LocalDateTime expiresAt;

    // User information
    private UserResponse user;

    // Security context
    private String[] authorities;   // User roles/permissions
    private boolean requiresPasswordChange;
    private boolean isFirstLogin;


}