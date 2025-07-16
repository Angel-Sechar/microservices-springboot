package com.ecommerce.campus.authservice.infrastructure.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Web DTO for user registration requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request")
public class RegisterRequest {

    @Schema(description = "User email address", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    @JsonProperty("email")
    private String email;

    @Schema(description = "User password (minimum 8 characters, must include uppercase, lowercase, number, and special character)",
            example = "StrongPassword123!")
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    @JsonProperty("password")
    private String password;

    @Schema(description = "User first name", example = "John")
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes")
    @JsonProperty("firstName")
    private String firstName;

    @Schema(description = "User last name", example = "Doe")
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes")
    @JsonProperty("lastName")
    private String lastName;

    @Schema(description = "User role (optional, defaults to USER)", example = "USER", allowableValues = {"USER", "ADMIN", "MODERATOR"})
    @Pattern(regexp = "^(USER|ADMIN|MODERATOR)$", message = "Role must be USER, ADMIN, or MODERATOR")
    @JsonProperty("role")
    private String role = "USER";

    @Schema(description = "Accept terms and conditions", example = "true")
    @AssertTrue(message = "You must accept the terms and conditions")
    @JsonProperty("acceptTerms")
    private Boolean acceptTerms;
}