package com.ecommerce.campus.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service to manage blacklisted JWT tokens in Redis.
 * Tokens are automatically removed when they expire.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlackListService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    /**
     * Add a token to the blacklist.
     * Token will be automatically removed when it expires.
     *
     * @param token The JWT token to blacklist
     * @param timeToLive Time until token expires
     */
    public void blacklistToken(String token, long timeToLive) {
        String key = BLACKLIST_PREFIX + token;

        // Store token with TTL matching its expiration time
        redisTemplate.opsForValue().set(
                key,
                "blacklisted",
                timeToLive,
                TimeUnit.SECONDS
        );

    }

    /**
     * Check if a token is blacklisted.
     *
     * @param token The JWT token to check
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));//to handle null
    }

    /**
     * Remove a token from the blacklist (if needed for admin purposes).
     *
     * @param token The JWT token to remove from blacklist
     */
    public void removeFromBlacklist(String token) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.delete(key);
    }

    /**
     * Get remaining TTL for a blacklisted token.
     *
     * @param token The JWT token
     * @return TTL in seconds, -1 if not found
     */
    public long getTokenTTL(String token) {
        String key = BLACKLIST_PREFIX + token;
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null ? ttl : -1;
    }
}