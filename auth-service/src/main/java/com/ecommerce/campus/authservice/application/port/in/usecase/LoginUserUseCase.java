package com.ecommerce.campus.authservice.application.port.in.usecase;

import com.ecommerce.campus.common.application.port.in.UseCase;
import com.ecommerce.campus.authservice.application.dto.command.LoginCommand;
import com.ecommerce.campus.authservice.application.dto.response.AuthenticationResponse;

/**
 * Use case for user authentication
 * Handles login validation and token generation
 */
public interface LoginUserUseCase extends UseCase<LoginCommand, AuthenticationResponse> {

    /**
     * Authenticate user and generate tokens
     *
     * @param command Login credentials
     * @return Authentication tokens and user info
     * @throws InvalidCredentialsException if credentials are invalid
     * @throws AccountLockedException if account is locked
     * @throws AccountNotActivatedException if account needs verification
     */
    @Override
    AuthenticationResponse execute(LoginCommand command);
}
