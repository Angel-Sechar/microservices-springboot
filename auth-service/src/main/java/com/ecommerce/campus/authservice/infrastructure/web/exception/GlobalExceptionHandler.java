package com.ecommerce.campus.authservice.infrastructure.web.exception;

import com.ecommerce.campus.authservice.application.exception.AuthApplicationException;
import com.ecommerce.campus.authservice.application.exception.InvalidTokenException;
import com.ecommerce.campus.authservice.application.exception.TokenGenerationException;
import com.ecommerce.campus.authservice.domain.exception.*;
import com.ecommerce.campus.authservice.infrastructure.web.dto.response.ApiResponse;
import com.ecommerce.campus.common.domain.exception.DomainException;
import com.ecommerce.campus.common.domain.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Global exception handler for the authentication service
 * Provides centralized error handling and consistent error responses
 */
@RestControllerAdvice
@Slf4j
@Order(1)
public class GlobalExceptionHandler {

    // ========================================
    // AUTHENTICATION & AUTHORIZATION ERRORS
    // ========================================

    @ExceptionHandler(AuthenticationDomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationDomainException(
            AuthenticationDomainException e, HttpServletRequest request) {

        log.warn("Authentication domain exception: {} at {}", e.getMessage(), request.getRequestURI());

        HttpStatus status = determineHttpStatusForAuthException(e);
        String errorCode = determineErrorCodeForAuthException(e);

        ApiResponse<Void> response = ApiResponse.failure(errorCode, formatUserFriendlyMessage(e.getMessage()));

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(
            Exception e, HttpServletRequest request) {

        log.warn("Invalid credentials attempt from IP: {} at {}",
                getClientIpAddress(request), request.getRequestURI());

        ApiResponse<Void> response = ApiResponse.failure(
                "INVALID_CREDENTIALS",
                "Invalid email or password"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(
            UserNotFoundException e, HttpServletRequest request) {

        log.warn("User not found: {} at {}", e.getMessage(), request.getRequestURI());

        // Return same message as invalid credentials for security
        ApiResponse<Void> response = ApiResponse.failure(
                "INVALID_CREDENTIALS",
                "Invalid email or password"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExists(
            UserAlreadyExistsException e, HttpServletRequest request) {

        log.warn("User registration conflict: {} at {}", e.getMessage(), request.getRequestURI());

        ApiResponse<Void> response = ApiResponse.failure(
                "EMAIL_ALREADY_EXISTS",
                "An account with this email address already exists"
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountLocked(
            AccountLockedException e, HttpServletRequest request) {

        log.warn("Account locked attempt: {} at {}", e.getMessage(), request.getRequestURI());

        ApiResponse<Void> response = ApiResponse.failure(
                "ACCOUNT_LOCKED",
                "Your account has been temporarily locked due to multiple failed login attempts. Please try again later."
        );

        return ResponseEntity.status(HttpStatus.LOCKED).body(response);
    }

    @ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountNotActivated(
            AccountNotActivatedException e, HttpServletRequest request) {

        log.warn("Account not activated: {} at {}", e.getMessage(), request.getRequestURI());

        ApiResponse<Void> response = ApiResponse.failure(
                "ACCOUNT_NOT_ACTIVATED",
                "Your account has not been activated. Please check your email and click the activation link."
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException e, HttpServletRequest request) {

        log.warn("Access denied: {} at {}", e.getMessage(), request.getRequestURI());

        ApiResponse<Void> response = ApiResponse.failure(
                "ACCESS_DENIED",
                "You do not have permission to access this resource"
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // ========================================
    // TOKEN HANDLING ERRORS
    // ========================================

    @ExceptionHandler({InvalidTokenException.class, TokenGenerationException.class})
    public ResponseEntity<ApiResponse<Void>> handleTokenException(
            Exception e, HttpServletRequest request) {

        log.warn("Token exception: {} at {}", e.getMessage(), request.getRequestURI());

        ApiResponse<Void> response = ApiResponse.failure(
                "INVALID_TOKEN",
                "Invalid or expired token"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // ========================================
    // VALIDATION ERRORS
    // ========================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        log.warn("Validation error at {}: {}", request.getRequestURI(), e.getMessage());

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Void> response = ApiResponse.failure(
                "VALIDATION_ERROR",
                "Invalid input data",
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException e, HttpServletRequest request) {

        log.warn("Constraint violation at {}: {}", request.getRequestURI(), e.getMessage());

        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();

        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }

        ApiResponse<Void> response = ApiResponse.failure(
                "VALIDATION_ERROR",
                "Invalid input data",
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainValidation(
            ValidationException e, HttpServletRequest request) {

        log.warn("Domain validation error at {}: {}", request.getRequestURI(), e.getMessage());

        ApiResponse<Void> response = ApiResponse.failure(
                e.getErrorCode(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ========================================
    // HTTP & REQUEST ERRORS
    // ========================================

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadable(
            HttpMessageNotReadableException e, HttpServletRequest request) {

        log.warn("Malformed request body at {}: {}", request.getRequestURI(), e.getMessage());

        ApiResponse<Void> response = ApiResponse.failure(
                "MALFORMED_REQUEST",
                "Invalid request format. Please check your request body."
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {

        log.warn("Method not supported at {}: {}", request.getRequestURI(), e.getMessage());

        ApiResponse<Void> response = ApiResponse.failure(
                "METHOD_NOT_SUPPORTED",
                String.format("HTTP method '%s' is not supported for this endpoint", e.getMethod())
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingHeader(
            MissingRequestHeaderException e, HttpServletRequest request) {

        log.warn("Missing required header at {}: {}", request.getRequestURI(), e.getMessage());

        ApiResponse<Void> response = ApiResponse.failure(
                "MISSING_HEADER",
                String.format("Required header '%s' is missing", e.getHeaderName())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameter(
            MissingServletRequestParameterException e, HttpServletRequest request) {

        log.warn("Missing required parameter at {}: {}", request.getRequestURI(), e.getMessage());

        ApiResponse<Void> response = ApiResponse.failure(
                "MISSING_PARAMETER",
                String.format("Required parameter '%s' is missing", e.getParameterName())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {

        log.warn("Type mismatch at {}: {}", request.getRequestURI(), e.getMessage());

        ApiResponse<Void> response = ApiResponse.failure(
                "TYPE_MISMATCH",
                String.format("Invalid value for parameter '%s'", e.getName())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ========================================
    // DATABASE ERRORS
    // ========================================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
            DataIntegrityViolationException e, HttpServletRequest request) {

        log.error("Data integrity violation at {}: {}", request.getRequestURI(), e.getMessage());

        String message = "Data conflict occurred";
        if (e.getMessage() != null && e.getMessage().contains("email")) {
            message = "Email address already exists";
        }

        ApiResponse<Void> response = ApiResponse.failure(
                "DATA_CONFLICT",
                message
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // ========================================
    // DOMAIN & APPLICATION ERRORS
    // ========================================

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(
            DomainException e, HttpServletRequest request) {

        log.warn("Domain exception at {}: {}", request.getRequestURI(), e.getMessage());

        ApiResponse<Void> response = ApiResponse.failure(
                e.getErrorCode(),
                formatUserFriendlyMessage(e.getMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AuthApplicationException.class)
    public ResponseEntity<ApiResponse<Void>> handleApplicationException(
            AuthApplicationException e, HttpServletRequest request) {

        log.warn("Application exception at {}: {}", request.getRequestURI(), e.getMessage());

        ApiResponse<Void> response = ApiResponse.failure(
                "APPLICATION_ERROR",
                formatUserFriendlyMessage(e.getMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ========================================
    // GENERIC ERROR HANDLER
    // ========================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception e, HttpServletRequest request) {

        log.error("Unexpected error at {}: {}", request.getRequestURI(), e.getMessage(), e);

        ApiResponse<Void> response = ApiResponse.failure(
                "INTERNAL_ERROR",
                "An unexpected error occurred. Please try again later."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    private HttpStatus determineHttpStatusForAuthException(AuthenticationDomainException e) {
        return switch (e.getClass().getSimpleName()) {
            case "InvalidCredentialsException" -> HttpStatus.UNAUTHORIZED;
            case "AccountLockedException" -> HttpStatus.LOCKED;
            case "AccountNotActivatedException" -> HttpStatus.FORBIDDEN;
            case "UserNotFoundException" -> HttpStatus.UNAUTHORIZED;
            case "UserAlreadyExistsException" -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };
    }

    private String determineErrorCodeForAuthException(AuthenticationDomainException e) {
        return switch (e.getClass().getSimpleName()) {
            case "InvalidCredentialsException" -> "INVALID_CREDENTIALS";
            case "AccountLockedException" -> "ACCOUNT_LOCKED";
            case "AccountNotActivatedException" -> "ACCOUNT_NOT_ACTIVATED";
            case "UserNotFoundException" -> "USER_NOT_FOUND";
            case "UserAlreadyExistsException" -> "USER_ALREADY_EXISTS";
            default -> "AUTHENTICATION_ERROR";
        };
    }

    private String formatUserFriendlyMessage(String technicalMessage) {
        if (technicalMessage == null) return "An error occurred";

        // Convert technical messages to user-friendly ones
        return switch (technicalMessage.toLowerCase()) {
            case "user not found" -> "Invalid email or password";
            case "invalid password" -> "Invalid email or password";
            case "password cannot be null" -> "Password is required";
            case "email cannot be null" -> "Email is required";
            default -> technicalMessage;
        };
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}