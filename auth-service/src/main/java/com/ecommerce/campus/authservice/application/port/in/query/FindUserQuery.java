package com.ecommerce.campus.authservice.application.port.in.query;

import com.ecommerce.campus.common.application.port.in.Query;
import com.ecommerce.campus.authservice.application.dto.request.FindUserRequest;
import com.ecommerce.campus.authservice.application.dto.response.UserResponse;

/**
 * Query for finding user information
 * Read-only operations for user data
 */
public interface FindUserQuery extends Query<FindUserRequest, UserResponse> {

    /**
     * Find user by ID or email
     *
     * @param request Find user request
     * @return User information
     * @throws UserNotFoundException if user doesn't exist
     */
    @Override
    UserResponse execute(FindUserRequest request);
}
