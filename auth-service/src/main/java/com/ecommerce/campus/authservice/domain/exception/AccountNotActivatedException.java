package com.ecommerce.campus.authservice.domain.exception;

public class AccountNotActivatedException extends AuthenticationDomainException {

    public AccountNotActivatedException() {
        super("Account is not activated. Please verify your email address");
    }
}