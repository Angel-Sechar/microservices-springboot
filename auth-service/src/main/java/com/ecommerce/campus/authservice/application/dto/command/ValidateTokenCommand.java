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
public class ValidateTokenCommand {

    @NotBlank(message = "Token is required")
    private String token;

    private boolean includeUserDetails; // Whether to include full user info in response
}