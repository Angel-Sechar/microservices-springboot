package com.ecommerce.campus.authservice.application.port.in.usecase;

import com.ecommerce.campus.common.application.port.in.UseCase;
import com.ecommerce.campus.authservice.application.dto.command.RegisterUserCommand;
import com.ecommerce.campus.authservice.application.dto.response.UserResponse;

/**
 * Use case for user registration
 * Handles the complete user registration flow
 */
public interface RegisterUserUseCase extends UseCase<RegisterUserCommand, UserResponse> {

    /**
     * Register a new user in the system
     *
     * @param command Registration details
     * @return Created user information
     * @throws UserAlreadyExistsException if email is already registered
     * @throws IllegalArgumentException if validation fails
     */
    @Override
    UserResponse execute(RegisterUserCommand command);
}
