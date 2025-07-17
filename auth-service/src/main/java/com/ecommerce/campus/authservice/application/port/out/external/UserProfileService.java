package com.ecommerce.campus.authservice.application.port.out.external;

import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;

/**
 * Port for user profile operations in external service
 * Will be implemented by UserProfileServiceAdapter using OpenFeign
 */
public interface UserProfileService {

    void createUserProfile(UserId userId, String firstName, String lastName, String email);

    void updateUserProfile(UserId userId, String firstName, String lastName);

    void deleteUserProfile(UserId userId);
}