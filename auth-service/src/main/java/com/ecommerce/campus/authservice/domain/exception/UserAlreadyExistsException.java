package com.ecommerce.campus.authservice.domain.exception;

import com.ecommerce.campus.authservice.domain.model.valueobject.Email;

public class UserAlreadyExistsException extends AuthenticationDomainException {

    public UserAlreadyExistsException(Email email) {
        super("User with email " + email.getValue() + " already exists");
    }
}
