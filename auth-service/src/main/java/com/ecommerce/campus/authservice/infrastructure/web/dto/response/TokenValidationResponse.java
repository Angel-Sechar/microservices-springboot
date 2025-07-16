
package com.ecommerce.campus.authservice.infrastructure.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Web DTO for token validation responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Token validation response")
public class TokenValidationResponse {

    @Schema(description = "Token validity status", example = "true")
    @JsonProperty("valid")
    private Boolean valid;

    @Schema(description = "User ID from token", example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonProperty("userId")
    private String userId;

    @Schema(description = "User email from token", example = "john.doe@example.com")
    @JsonProperty("email")
    private String email;

    @Schema(description = "User role from token", example = "USER")
    @JsonProperty("role")
    private String role;

    @Schema(description = "User authorities/permissions", example = "[\"USER\"]")
    @JsonProperty("authorities")
    private String[] authorities;

    @Schema(description = "Token expiration timestamp", example = "2024-01-15T15:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("expiresAt")
    private LocalDateTime expiresAt;

    @Schema(description = "Whether token is expired", example = "false")
    @JsonProperty("expired")
    private Boolean expired;

    @Schema(description = "Full user details (if requested)")
    @JsonProperty("userDetails")
    private UserResponse userDetails;

    @Schema(description = "Error code for invalid tokens", example = "TOKEN_EXPIRED")
    @JsonProperty("errorCode")
    private String errorCode;

    @Schema(description = "Error message for invalid tokens", example = "Token has expired")
    @JsonProperty("errorMessage")
    private String errorMessage;

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

    public static TokenValidationResponse invalid(String errorCode, String errorMessage) {
        return TokenValidationResponse.builder()
                .valid(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .expired(true)
                .build();
    }
}