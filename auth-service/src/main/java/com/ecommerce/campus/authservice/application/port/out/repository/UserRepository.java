package com.ecommerce.campus.authservice.application.port.out.repository;

import com.ecommerce.campus.common.application.port.out.Repository;
import com.ecommerce.campus.authservice.domain.model.aggregate.User;
import com.ecommerce.campus.authservice.domain.model.valueobject.Email;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;

import java.util.Optional;

/**
 * Repository port for User aggregate
 * Infrastructure will implement this interface
 */
public interface UserRepository extends Repository<User, UserId> {

    /**
     * Find user by email address
     */
    Optional<User> findByEmail(Email email);

    /**
     * Check if user exists with given email
     */
    boolean existsByEmail(Email email);

    /**
     * Find user by email for authentication (may include additional security info)
     */
    Optional<User> findByEmailForAuthentication(Email email);
}
