package com.ecommerce.campus.authservice.domain.exception;

import com.ecommerce.campus.authservice.domain.model.valueobject.Email;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;

public class UserNotFoundException extends AuthenticationDomainException {

    public UserNotFoundException(UserId userId) {
        super("User with ID " + userId.getValue() + " not found");
    }

    public UserNotFoundException(Email email) {
        super("User with email " + email.getValue() + " not found");
    }
}