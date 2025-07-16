package com.ecommerce.campus.authservice.infrastructure.persistence.jpa.repository;

import com.ecommerce.campus.authservice.infrastructure.persistence.jpa.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for User entities
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    /**
     * Find user by email (case-insensitive)
     */
    @Query("SELECT u FROM UserJpaEntity u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<UserJpaEntity> findByEmailIgnoreCase(@Param("email") String email);

    /**
     * Check if user exists by email
     */
    @Query("SELECT COUNT(u) > 0 FROM UserJpaEntity u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    /**
     * Find active users by email (for authentication)
     */
    @Query("SELECT u FROM UserJpaEntity u WHERE LOWER(u.email) = LOWER(:email) " +
            "AND u.status IN ('ACTIVE', 'PENDING_VERIFICATION')")
    Optional<UserJpaEntity> findActiveUserByEmail(@Param("email") String email);
}