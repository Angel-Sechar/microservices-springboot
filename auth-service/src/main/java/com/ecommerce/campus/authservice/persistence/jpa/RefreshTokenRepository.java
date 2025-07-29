package com.ecommerce.campus.authservice.persistence.jpa;

import com.ecommerce.campus.authservice.model.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    // Clean up expired tokens
    @Transactional
    @Modifying
    @Procedure(procedureName = "DBO.SP_REFRESH_TOKEN_DEL_EXPIRED_TOKENS")
    //@Query(value = "EXEC DBO.SP_REFRESH_TOKEN_DEL_EXPIRED_TOKENS @ExpirationDate = :ExpirationDate", nativeQuery = true)
    int deleteExpiredTokens(@Param("ExpirationDate") LocalDateTime now);

    // Delete all tokens for a user (logout from all devices)
    //thinking how to indicate by device
    @Transactional
    @Modifying
    @Procedure(procedureName = "DBO.SP_REFRESH_TOKEN_DEL_LOGOUT_USER")
    void deleteTokensByUserId(@Param("UserId") Long userId);

}