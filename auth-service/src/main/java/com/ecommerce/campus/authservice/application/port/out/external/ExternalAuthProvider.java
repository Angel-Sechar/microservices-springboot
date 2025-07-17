package com.ecommerce.campus.authservice.application.port.out.external;

/**
 * Port for external authentication provider validation
 * Will be implemented by ExternalAuthProviderAdapter using OpenFeign
 */
public interface ExternalAuthProvider {

    boolean validateGoogleToken(String accessToken);

    boolean validateFacebookToken(String accessToken);

    default boolean validateExternalToken(String provider, String accessToken) {
        return switch (provider.toLowerCase()) {
            case "google" -> validateGoogleToken(accessToken);
            case "facebook" -> validateFacebookToken(accessToken);
            default -> false;
        };
    }
}
