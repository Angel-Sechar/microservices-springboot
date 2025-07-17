package com.ecommerce.campus.authservice.infrastructure.external.config;

import com.ecommerce.campus.authservice.infrastructure.external.exception.ExternalServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom error decoder for Feign clients
 * Converts HTTP errors to domain-specific exceptions
 */
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.warn("External service call failed: {} - Status: {}", methodKey, response.status());

        switch (response.status()) {
            case 400:
                return new ExternalServiceException("Bad request to external service: " + methodKey);
            case 401:
                return new ExternalServiceException("Unauthorized access to external service: " + methodKey);
            case 403:
                return new ExternalServiceException("Forbidden access to external service: " + methodKey);
            case 404:
                return new ExternalServiceException("External service not found: " + methodKey);
            case 429:
                return new ExternalServiceException("Rate limit exceeded for external service: " + methodKey);
            case 500:
                return new ExternalServiceException("Internal error in external service: " + methodKey);
            case 503:
                return new ExternalServiceException("External service unavailable: " + methodKey);
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}