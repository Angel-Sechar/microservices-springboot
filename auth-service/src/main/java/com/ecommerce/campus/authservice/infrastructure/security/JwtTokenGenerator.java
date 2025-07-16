package com.ecommerce.campus.authservice.infrastructure.security;

import com.ecommerce.campus.authservice.application.port.out.security.TokenGenerator;
import com.ecommerce.campus.authservice.domain.model.aggregate.User;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT implementation of TokenGenerator port
 * Uses JJWT library with HMAC-SHA256 signing
 */
@Slf4j
@Component
public class JwtTokenGenerator implements TokenGenerator {

    private final SecretKey secretKey;
    private final Duration defaultAccessTokenExpiration;
    private final Duration refreshTokenExpiration;
    private final String issuer;

    public JwtTokenGenerator(
            @Value("${auth.jwt.secret:campus-ecommerce-super-secret-key-minimum-256-bits-for-hs256-algorithm}") String secret,
            @Value("${auth.jwt.access-token-expiration:PT24H}") Duration accessTokenExpiration,
            @Value("${auth.jwt.refresh-token-expiration:P30D}") Duration refreshTokenExpiration,
            @Value("${auth.jwt.issuer:campus-ecommerce}") String issuer
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.defaultAccessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.issuer = issuer;

        log.info("JWT Token Generator initialized - Access: {}, Refresh: {}, Issuer: {}",
                accessTokenExpiration, refreshTokenExpiration, issuer);
    }

    @Override
    public String generateAccessToken(User user) {
        return generateAccessToken(user, defaultAccessTokenExpiration);
    }

    @Override
    public String generateAccessToken(User user, Duration expiration) {
        log.debug("Generating access token for user: {} with expiration: {}", user.getId(), expiration);

        Instant now = Instant.now();
        Instant expirationTime = now.plus(expiration);

        return Jwts.builder()
                .subject(user.getId().toString())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expirationTime))
                .claim("email", user.getEmail().getValue())
                .claim("role", user.getRole().getCode())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .claim("status", user.getStatus().getCode())
                .claim("type", "access")
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(User user) {
        log.debug("Generating refresh token for user: {}", user.getId());

        Instant now = Instant.now();
        Instant expirationTime = now.plus(refreshTokenExpiration);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expirationTime))
                .claim("email", user.getEmail().getValue())
                .claim("type", "refresh")
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public UserId validateTokenAndGetUserId(String token) {
        try {
            Claims claims = parseAndValidateToken(token);
            String userIdString = claims.getSubject();

            if (userIdString == null || userIdString.isBlank()) {
                throw new JwtException("Token missing subject claim");
            }

            return UserId.of(userIdString);

        } catch (JwtException e) {
            log.warn("Invalid token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> extractClaims(String token) {
        try {
            Claims claims = parseAndValidateToken(token);
            return new HashMap<>(claims);

        } catch (JwtException e) {
            log.warn("Failed to extract claims from token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseAndValidateToken(token);
            return claims.getExpiration().before(new Date());

        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            // Invalid token is considered expired
            return true;
        }
    }

    @Override
    public long getTokenExpirationTime(String token) {
        try {
            Claims claims = parseAndValidateToken(token);
            return claims.getExpiration().getTime() / 1000; // Unix timestamp

        } catch (JwtException e) {
            log.warn("Failed to get expiration time from token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token: " + e.getMessage(), e);
        }
    }

    private Claims parseAndValidateToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
