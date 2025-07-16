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
 * Web DTO for authentication responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Authentication response with tokens and user info")
public class AuthResponse {

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIs...")
    @JsonProperty("accessToken")
    private String accessToken;

    @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiIs...")
    @JsonProperty("refreshToken")
    private String refreshToken;

    @Schema(description = "Token type", example = "Bearer")
    @JsonProperty("tokenType")
    private String tokenType;

    @Schema(description = "Token expiration time in seconds", example = "3600")
    @JsonProperty("expiresIn")
    private Long expiresIn;

    @Schema(description = "Token expiration timestamp", example = "2024-01-15T15:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("expiresAt")
    private LocalDateTime expiresAt;

    @Schema(description = "Authenticated user information")
    @JsonProperty("user")
    private UserResponse user;

    @Schema(description = "Additional session information")
    @JsonProperty("sessionInfo")
    private SessionInfo sessionInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Session information")
    public static class SessionInfo {

        @Schema(description = "Session creation timestamp", example = "2024-01-15T14:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @JsonProperty("loginTime")
        private LocalDateTime loginTime;

        @Schema(description = "Client IP address", example = "192.168.1.100")
        @JsonProperty("ipAddress")
        private String ipAddress;

        @Schema(description = "User agent string", example = "Mozilla/5.0...")
        @JsonProperty("userAgent")
        private String userAgent;

        @Schema(description = "Is remember me session", example = "false")
        @JsonProperty("rememberMe")
        private Boolean rememberMe;
    }
}