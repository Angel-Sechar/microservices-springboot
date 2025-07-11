package com.ecommerce.campus.common.domain.exception;

/**
 * Exception thrown when domain validation fails.
 */
public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }

    public ValidationException(String field, String message) {
        super(String.format("Validation failed for field '%s': %s", field, message), "FIELD_VALIDATION_ERROR");
    }
}