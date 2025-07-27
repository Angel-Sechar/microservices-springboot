package com.ecommerce.campus.authservice.persistence.jpa;

import com.ecommerce.campus.authservice.model.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByToken(String token);
        //Move to sp after
    // Clean up expired tokens
    @Modifying
    @Query("DELETE FROM AuthToken rt WHERE rt.expiryDate < :now")
    int deleteExpiredTokens(LocalDateTime now);

    // Delete all tokens for a user (logout from all devices)
    //thinking how to indicate devices
    @Modifying
    @Query("DELETE FROM AuthToken rt WHERE rt.user.id = :userId")
    int deleteByUserId(Long userId);

}