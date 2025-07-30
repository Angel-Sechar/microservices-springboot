package com.ecommerce.campus.authservice.service;

import com.ecommerce.campus.authservice.dto.*;
import com.ecommerce.campus.authservice.exception.AuthException;
import com.ecommerce.campus.authservice.model.RefreshToken;
import com.ecommerce.campus.authservice.model.Role;
import com.ecommerce.campus.authservice.model.User;
import com.ecommerce.campus.authservice.persistence.jpa.RefreshTokenRepository;
import com.ecommerce.campus.authservice.persistence.jpa.UserRepository;
import com.ecommerce.campus.authservice.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor //Lombok annotation to implicit constructor at compile time
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final TokenBlackListService tokenBlacklistService;

    /**
     * TokenResponse
      * @param request The Login data to generate token
     * @return TokenResponse the token generated
     */
    @Transactional
    public TokenResponse login(LoginRequest request) {
        try {
            // Authenticate user - it internally matches the hashed password with the one in repository
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            User user = (User) authentication.getPrincipal();

            // Update last login
            userRepository.updateLastLogin(user.getUserId(), LocalDateTime.now());

            // Generate tokens
            String accessToken = generateAccessToken(user);
            RefreshToken refreshToken = createRefreshToken(user);

            log.info("User {} logged in successfully", user.getUsername());

            return new TokenResponse(
                    accessToken,
                    refreshToken.getToken(),
                    jwtProvider.getAccessTokenExpirationMs() / 1000, // Convert to seconds
                    UserResponse.from(user)
            );

        } catch (AuthenticationException e) {
            log.error("Login failed for user: {}", request.username());
            throw new AuthException("Invalid username or password");
        }
    }

    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new AuthException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new AuthException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        String newAccessToken = generateAccessToken(user);

        log.info("Token refreshed for user: {}", user.getUsername());

        return new TokenResponse(
                newAccessToken,
                refreshToken.getToken(),
                jwtProvider.getAccessTokenExpirationMs() / 1000,
                UserResponse.from(user)
        );
    }


    @Transactional
//    @CacheEvict(value = "users", key = "#userId")
    public void logout(Long userId, String token) {
        // Delete refresh token associated with this session
        refreshTokenRepository.deleteTokensByUserId(userId);

        // Get Time To Live from JWT Token
        long timeToLive = jwtProvider.getTimeToLiveMs(token) / 1000;

        // Add token to BlackList - redis
        tokenBlacklistService.blacklistToken(token, timeToLive);
    }

    @Transactional
//    @CacheEvict(value = "user", key = "#userId")
    public void logoutFromAllDevices(Long userId) {
        refreshTokenRepository.deleteTokensByUserId(userId);
        //thinking to consider devices
    }

    private String generateAccessToken(User user) {
        String roles = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.joining(","));

        return jwtProvider.generateAccessToken(user.getUserId(), user.getUsername(), roles);
    }

    private RefreshToken createRefreshToken(User user) {
        // Delete old refresh tokens - just for one device at the same time
        refreshTokenRepository.deleteTokensByUserId(user.getUserId());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(jwtProvider.generateRefreshToken(user.getUserId()))
                .user(user)
                .expirationAt(LocalDateTime.now().plusSeconds(jwtProvider.getAccessTokenExpirationMs()/1000))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }
}