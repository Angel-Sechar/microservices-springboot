package com.ecommerce.campus.authservice.application.dto.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivateUserCommand {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Verification token is required")
    private String verificationToken;
}