package com.ecommerce.campus.authservice.domain.exception;

/**
 * Base exception for authentication domain
 */
public abstract class AuthenticationDomainException extends RuntimeException {

    protected AuthenticationDomainException(String message) {
        super(message);
    }

    protected AuthenticationDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
