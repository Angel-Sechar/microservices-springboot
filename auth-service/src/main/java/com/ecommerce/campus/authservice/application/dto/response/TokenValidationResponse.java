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
public class TokenValidationResponse {

    private boolean valid;
    private String userId;
    private String email;
    private String role;
    private String[] authorities;
    private LocalDateTime expiresAt;
    private boolean expired;

    // Optional full user details (if requested)
    private UserResponse userDetails;

    // Error details for invalid tokens
    private String errorCode;
    private String errorMessage;

    public static TokenValidationResponse invalid(String errorCode, String errorMessage) {
        return TokenValidationResponse.builder()
                .valid(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }

    public static TokenValidationResponse valid(String userId, String email, String role,
                                                String[] authorities, LocalDateTime expiresAt) {
        return TokenValidationResponse.builder()
                .valid(true)
                .userId(userId)
                .email(email)
                .role(role)
                .authorities(authorities)
                .expiresAt(expiresAt)
                .expired(false)
                .build();
    }
}
