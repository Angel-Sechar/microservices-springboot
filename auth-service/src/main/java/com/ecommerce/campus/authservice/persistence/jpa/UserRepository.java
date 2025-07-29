package com.ecommerce.campus.authservice.persistence.jpa;

import com.ecommerce.campus.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Simple queries - let Spring Data generate them
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Update last login - simple and direct (create sp urgent)
    @Modifying
    @Procedure(procedureName = "DBO.SP_AUTH_USER_UPD_LASTLOGIN")
    void updateLastLogin(@Param("UserId") Long userId, @Param("LastLogin") LocalDateTime lastLogin);
}