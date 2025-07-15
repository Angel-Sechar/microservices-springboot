package com.ecommerce.campus.authservice.application.exception;

public class InvalidTokenException extends AuthApplicationException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}