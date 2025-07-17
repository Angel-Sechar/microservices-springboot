package com.ecommerce.campus.authservice.infrastructure.external.adapter;

import com.ecommerce.campus.authservice.application.port.out.external.UserProfileService;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;
import com.ecommerce.campus.authservice.infrastructure.external.client.UserProfileFeignClient;
import com.ecommerce.campus.authservice.infrastructure.external.mapper.ExternalServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adapter for user profile service using OpenFeign
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceAdapter implements UserProfileService {

    private final UserProfileFeignClient profileClient;
    private final ExternalServiceMapper mapper;

    @Override
    public void createUserProfile(UserId userId, String firstName, String lastName, String email) {
        try {
            var request = mapper.toCreateProfileRequest(userId, firstName, lastName, email);
            var response = profileClient.createProfile(request);

            log.info("User profile created successfully for user: {} with profileId: {}",
                    userId.getValue(), response.getProfileId());

        } catch (Exception e) {
            log.error("Failed to create user profile for {}: {}", userId.getValue(), e.getMessage());
            // Decide whether to fail user registration or continue
            throw new ProfileCreationException("Failed to create user profile", e);
        }
    }

    @Override
    public void updateUserProfile(UserId userId, String firstName, String lastName) {
        try {
            var request = mapper.toUpdateProfileRequest(firstName, lastName);
            var response = profileClient.updateProfile(userId.getValue(), request);

            log.info("User profile updated successfully for user: {}", userId.getValue());

        } catch (Exception e) {
            log.error("Failed to update user profile for {}: {}", userId.getValue(), e.getMessage());
            throw new ProfileUpdateException("Failed to update user profile", e);
        }
    }

    @Override
    public void deleteUserProfile(UserId userId) {
        try {
            profileClient.deleteProfile(userId.getValue());
            log.info("User profile deleted successfully for user: {}", userId.getValue());

        } catch (Exception e) {
            log.error("Failed to delete user profile for {}: {}", userId.getValue(), e.getMessage());
            throw new ProfileDeletionException("Failed to delete user profile", e);
        }
    }
}