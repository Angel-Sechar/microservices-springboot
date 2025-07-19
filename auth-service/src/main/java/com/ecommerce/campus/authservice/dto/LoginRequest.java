package com.ecommerce.campus.authservice.dto;

import jakarta.validation.constraints.NotBlank;

// LoginRequest.java - Using record for immutability
public record LoginRequest(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {}