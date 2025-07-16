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
 * Web DTO for user information responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User information response")
public class UserResponse {

    @Schema(description = "User unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonProperty("userId")
    private String userId;

    @Schema(description = "User email address", example = "john.doe@example.com")
    @JsonProperty("email")
    private String email;

    @Schema(description = "User first name", example = "John")
    @JsonProperty("firstName")
    private String firstName;

    @Schema(description = "User last name", example = "Doe")
    @JsonProperty("lastName")
    private String lastName;

    @Schema(description = "User full name", example = "John Doe")
    @JsonProperty("fullName")
    private String fullName;

    @Schema(description = "User role", example = "USER", allowableValues = {"USER", "ADMIN", "MODERATOR"})
    @JsonProperty("role")
    private String role;

    @Schema(description = "User account status", example = "ACTIVE", allowableValues = {"ACTIVE", "PENDING_VERIFICATION", "SUSPENDED", "LOCKED", "INACTIVE"})
    @JsonProperty("status")
    private String status;

    @Schema(description = "Whether user can login", example = "true")
    @JsonProperty("canLogin")
    private Boolean canLogin;

    @Schema(description = "Account creation timestamp", example = "2024-01-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Last login timestamp", example = "2024-01-15T14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("lastLoginAt")
    private LocalDateTime lastLoginAt;

    @Schema(description = "Profile last update timestamp", example = "2024-01-15T14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}