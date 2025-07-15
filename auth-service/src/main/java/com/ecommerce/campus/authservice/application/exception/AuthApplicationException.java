package com.ecommerce.campus.authservice.application.exception;

/**
 * Base exception for application layer
 */
public abstract class AuthApplicationException extends RuntimeException {

  protected AuthApplicationException(String message) {
    super(message);
  }

  protected AuthApplicationException(String message, Throwable cause) {
    super(message, cause);
  }
}