package com.ecommerce.campus.authservice.service;

import com.ecommerce.campus.authservice.persistence.jpa.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // Clean up expired tokens every hour
    @Scheduled(fixedDelay = 3600000) // 1 hour
    @Transactional
    public void cleanupExpiredTokens() {
        int deleted = refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        if (deleted > 0) {
            log.info("Cleaned up {} expired refresh tokens", deleted);
        }
    }
}