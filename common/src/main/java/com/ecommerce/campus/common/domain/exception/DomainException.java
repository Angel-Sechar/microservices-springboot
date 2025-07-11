package com.ecommerce.campus.common.domain.exception;

/**
 * Base exception for all domain-related errors.
 */
public abstract class DomainException extends RuntimeException {

    private final String errorCode;

    protected DomainException(String message) {
        super(message);
        this.errorCode = this.getClass().getSimpleName();
    }

    protected DomainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = this.getClass().getSimpleName();
    }

    protected DomainException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}