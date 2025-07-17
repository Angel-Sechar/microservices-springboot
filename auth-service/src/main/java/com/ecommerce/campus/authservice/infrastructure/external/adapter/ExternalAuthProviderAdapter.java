package com.ecommerce.campus.authservice.infrastructure.external.adapter;

import com.ecommerce.campus.authservice.application.port.out.external.ExternalAuthProvider;
import com.ecommerce.campus.authservice.infrastructure.external.client.GoogleAuthFeignClient;
import com.ecommerce.campus.authservice.infrastructure.external.client.FacebookAuthFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adapter for external authentication providers using OpenFeign
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalAuthProviderAdapter implements ExternalAuthProvider {

    private final GoogleAuthFeignClient googleClient;
    private final FacebookAuthFeignClient facebookClient;

    @Override
    public boolean validateGoogleToken(String accessToken) {
        try {
            var response = googleClient.validateToken(accessToken);
            boolean isValid = response.isValid();

            log.debug("Google token validation result: {}", isValid);
            return isValid;

        } catch (Exception e) {
            log.warn("Google token validation failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validateFacebookToken(String accessToken) {
        try {
            var response = facebookClient.validateToken(accessToken);
            boolean isValid = response.isValid();

            log.debug("Facebook token validation result: {}", isValid);
            return isValid;

        } catch (Exception e) {
            log.warn("Facebook token validation failed: {}", e.getMessage());
            return false;
        }
    }
}