package com.ecommerce.campus.authservice.domain.exception;

public class InvalidCredentialsException extends AuthenticationDomainException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}