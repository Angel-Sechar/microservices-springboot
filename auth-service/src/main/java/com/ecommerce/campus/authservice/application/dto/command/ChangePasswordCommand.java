package com.ecommerce.campus.authservice.application.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command for changing user password
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordCommand {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String newPassword;
}
