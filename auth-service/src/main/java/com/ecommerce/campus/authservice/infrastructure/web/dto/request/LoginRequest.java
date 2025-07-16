package com.ecommerce.campus.authservice.infrastructure.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Web DTO for user login requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User login request")
public class LoginRequest {

    @Schema(description = "User email address", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @JsonProperty("email")
    private String email;

    @Schema(description = "User password", example = "StrongPassword123!")
    @NotBlank(message = "Password is required")
    @Size(max = 128, message = "Password cannot exceed 128 characters")
    @JsonProperty("password")
    private String password;

    @Schema(description = "Remember me option for extended session", example = "false")
    @JsonProperty("rememberMe")
    private Boolean rememberMe = false;

    @Schema(description = "Client device information (optional)", example = "Mozilla/5.0...")
    @Size(max = 500, message = "Device info cannot exceed 500 characters")
    @JsonProperty("deviceInfo")
    private String deviceInfo;
}