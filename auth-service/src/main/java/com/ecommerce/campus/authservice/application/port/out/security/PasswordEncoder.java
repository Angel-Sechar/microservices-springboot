package com.ecommerce.campus.authservice.application.port.out.security;

import com.ecommerce.campus.authservice.domain.model.valueobject.Password;

/**
 * Port for password encoding operations
 * Infrastructure will implement using BCrypt
 */
public interface PasswordEncoder {

    /**
     * Encode raw password
     */
    Password encode(Password rawPassword);

    /**
     * Verify raw password against encoded password
     */
    boolean matches(Password rawPassword, Password encodedPassword);

    /**
     * Check if password needs re-encoding (for security upgrades)
     */
    boolean needsReEncoding(Password encodedPassword);
}