package com.ecommerce.campus.authservice.infrastructure.persistence.adapter;

import com.ecommerce.campus.authservice.application.port.out.repository.UserRepository;
import com.ecommerce.campus.authservice.domain.model.aggregate.User;
import com.ecommerce.campus.authservice.domain.model.valueobject.Email;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;
import com.ecommerce.campus.authservice.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.ecommerce.campus.authservice.infrastructure.persistence.jpa.mapper.UserPersistenceMapper;
import com.ecommerce.campus.authservice.infrastructure.persistence.jpa.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository adapter implementing UserRepository port
 * Translates between domain objects and JPA entities
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserPersistenceMapper mapper;

    @Override
    public User save(User user) {
        log.debug("Saving user: {}", user.getId());

        // Convert domain to JPA entity
        UserJpaEntity entity = mapper.toEntity(user);

        // Save through JPA
        UserJpaEntity savedEntity = jpaRepository.save(entity);

        // Convert back to domain
        User savedUser = mapper.toDomain(savedEntity);

        log.debug("User saved successfully: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    public Optional<User> findById(UserId id) {
        log.debug("Finding user by ID: {}", id);

        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        log.debug("Finding user by email: {}", email);

        return jpaRepository.findByEmailIgnoreCase(email.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        log.debug("Checking if user exists by email: {}", email);

        return jpaRepository.existsByEmailIgnoreCase(email.getValue());
    }

    @Override
    public Optional<User> findByEmailForAuthentication(Email email) {
        log.debug("Finding user by email for authentication: {}", email);

        return jpaRepository.findActiveUserByEmail(email.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(UserId id) {
        return jpaRepository.existsById(id.getValue());
    }

    @Override
    public void deleteById(UserId id) {
        log.debug("Deleting user by ID: {}", id);
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public List<User> findAll() {
        log.debug("Finding all users");

        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
