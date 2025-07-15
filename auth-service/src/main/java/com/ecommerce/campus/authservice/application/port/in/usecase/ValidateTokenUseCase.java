package com.ecommerce.campus.authservice.application.port.in.usecase;

import com.ecommerce.campus.common.application.port.in.UseCase;
import com.ecommerce.campus.authservice.application.dto.command.ValidateTokenCommand;
import com.ecommerce.campus.authservice.application.dto.response.TokenValidationResponse;

/**
 * Use case for JWT token validation
 * Used by other microservices to validate tokens
 */
public interface ValidateTokenUseCase extends UseCase<ValidateTokenCommand, TokenValidationResponse> {

    /**
     * Validate JWT token and return user context
     *
     * @param command Token validation request
     * @return Token validation result with user context
     * @throws InvalidTokenException if token is invalid or expired
     */
    @Override
    TokenValidationResponse execute(ValidateTokenCommand command);
}