package com.ecommerce.campus.authservice.infrastructure.external.client;

import com.ecommerce.campus.authservice.infrastructure.external.dto.request.TokenValidationRequest;
import com.ecommerce.campus.authservice.infrastructure.external.dto.response.TokenValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for external authentication providers
 */
@FeignClient(
        name = "google-auth",
        url = "https://oauth2.googleapis.com",
        configuration = ExternalAuthConfig.class
)
public interface GoogleAuthFeignClient {

    @GetMapping("/tokeninfo")
    TokenValidationResponse validateToken(@RequestParam("access_token") String accessToken);
}

@FeignClient(
        name = "facebook-auth",
        url = "https://graph.facebook.com",
        configuration = ExternalAuthConfig.class
)
public interface FacebookAuthFeignClient {

    @GetMapping("/me")
    TokenValidationResponse validateToken(@RequestParam("access_token") String accessToken);
}