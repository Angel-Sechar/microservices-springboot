package com.ecommerce.campus.authservice.application.service;

import com.ecommerce.campus.authservice.application.port.in.query.FindUserQuery;
import com.ecommerce.campus.authservice.application.port.out.repository.UserRepository;
import com.ecommerce.campus.authservice.application.dto.request.FindUserRequest;
import com.ecommerce.campus.authservice.application.dto.response.UserResponse;
import com.ecommerce.campus.authservice.application.mapper.AuthServiceMapper;
import com.ecommerce.campus.authservice.domain.model.aggregate.User;
import com.ecommerce.campus.authservice.domain.model.valueobject.Email;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;
import com.ecommerce.campus.authservice.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Query service for user information retrieval
 * Read-only operations for user data
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService implements FindUserQuery {

    private final UserRepository userRepository;
    private final AuthServiceMapper userMapper;

    @Override
    public UserResponse execute(FindUserRequest request) {
        if (!request.isValid()) {
            throw new IllegalArgumentException("Either userId or email must be provided, but not both");
        }

        User user;

        if (request.getUserId() != null) {
            // Find by user ID
            UserId userId = UserId.of(request.getUserId());
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
            log.debug("Found user by ID: {}", userId);

        } else {
            // Find by email
            Email email = Email.of(request.getEmail());
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException(email));
            log.debug("Found user by email: {}", email);
        }

        return userMapper.toResponse(user);
    }
}