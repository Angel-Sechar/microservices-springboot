package com.ecommerce.campus.authservice.application.exception;

public class TokenGenerationException extends AuthApplicationException {

    public TokenGenerationException(String message) {
        super(message);
    }

    public TokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}