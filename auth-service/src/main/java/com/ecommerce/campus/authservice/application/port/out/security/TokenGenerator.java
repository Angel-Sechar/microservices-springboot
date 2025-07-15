package com.ecommerce.campus.authservice.application.port.out.security;

import com.ecommerce.campus.authservice.domain.model.aggregate.User;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;

import java.time.Duration;
import java.util.Map;

/**
 * Port for JWT token operations
 * Infrastructure will implement using JJWT library
 */
public interface TokenGenerator {

    /**
     * Generate access token for user
     */
    String generateAccessToken(User user);

    /**
     * Generate access token with custom expiration
     */
    String generateAccessToken(User user, Duration expiration);

    /**
     * Generate refresh token for user
     */
    String generateRefreshToken(User user);

    /**
     * Validate token and extract user ID
     */
    UserId validateTokenAndGetUserId(String token);

    /**
     * Extract all claims from token
     */
    Map<String, Object> extractClaims(String token);

    /**
     * Check if token is expired
     */
    boolean isTokenExpired(String token);

    /**
     * Get token expiration time
     */
    long getTokenExpirationTime(String token);
}