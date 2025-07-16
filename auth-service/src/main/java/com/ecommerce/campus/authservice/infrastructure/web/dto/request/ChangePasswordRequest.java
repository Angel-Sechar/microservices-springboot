package com.ecommerce.campus.authservice.infrastructure.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Web DTO for password change requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Change password request")
public class ChangePasswordRequest {

    @Schema(description = "Current password", example = "OldPassword123!")
    @NotBlank(message = "Current password is required")
    @JsonProperty("currentPassword")
    private String currentPassword;

    @Schema(description = "New password (minimum 8 characters, must include uppercase, lowercase, number, and special character)",
            example = "NewStrongPassword456!")
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 128, message = "New password must be between 8 and 128 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$",
            message = "New password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    @JsonProperty("newPassword")
    private String newPassword;

    @Schema(description = "Confirm new password", example = "NewStrongPassword456!")
    @NotBlank(message = "Password confirmation is required")
    @JsonProperty("confirmPassword")
    private String confirmPassword;

    // Custom validation can be added here or in a validator class
    public boolean isPasswordConfirmationValid() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}
