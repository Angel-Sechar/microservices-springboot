package com.ecommerce.campus.authservice.application.dto.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginCommand {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String ipAddress;     // For security tracking
    private String userAgent;    // For security tracking
    private boolean rememberMe;  // For extended token expiration
    private String deviceInfo;
}