package com.ecommerce.campus.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.Transient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtProvider {

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // Generate access token with minimal claims for performance
    public String generateAccessToken(Long userId, String username, String roles) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plus(Duration.ofMillis(refreshTokenExpiration));

        Date issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date expiresAt = Date.from(expiryDate.atZone(ZoneId.systemDefault()).toInstant());

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", roles); // Single string, not array for smaller size

        return Jwts.builder()
                .subject(userId.toString()) // Just user ID, fetch details if needed
                .claims(claims)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(this.key)
                .compact();
    }

    // Simple refresh token - just a signed token with user ID
    public String generateRefreshToken(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plus(Duration.ofMillis(refreshTokenExpiration));

        Date issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date expiresAt = Date.from(expiryDate.atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(this.key)
                .compact();
    }

    // Validate token - simple and fast
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Get user ID from token
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.parseLong(claims.getSubject());
    }

    // Get roles from token
    public String getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    public long getTimeToLiveMs(String token){
        LocalDateTime now = LocalDateTime.now();

        Claims claims = getClaimsFromToken(token);
        LocalDateTime expiration = claims.getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return Duration.between(now, expiration).toMillis();
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpiration;
    }

}