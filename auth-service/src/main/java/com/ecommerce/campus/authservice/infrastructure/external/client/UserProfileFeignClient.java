package com.ecommerce.campus.authservice.infrastructure.external.client;

import com.ecommerce.campus.authservice.infrastructure.external.dto.request.CreateProfileRequest;
import com.ecommerce.campus.authservice.infrastructure.external.dto.request.UpdateProfileRequest;
import com.ecommerce.campus.authservice.infrastructure.external.dto.response.ProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client for user profile service communication
 */
@FeignClient(
        name = "user-profile-service",
        path = "/api/v1/profiles",
        configuration = FeignClientConfig.class
)
public interface UserProfileFeignClient {

    @PostMapping
    ProfileResponse createProfile(@RequestBody CreateProfileRequest request);

    @PutMapping("/{userId}")
    ProfileResponse updateProfile(@PathVariable String userId, @RequestBody UpdateProfileRequest request);

    @GetMapping("/{userId}")
    ProfileResponse getProfile(@PathVariable String userId);

    @DeleteMapping("/{userId}")
    void deleteProfile(@PathVariable String userId);
}
