package com.ecommerce.campus.authservice.domain.model.valueobject;

import com.ecommerce.campus.common.domain.model.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

/**
 * Password value object with strength validation
 * Handles both raw and hashed passwords
 */
@EqualsAndHashCode(callSuper = false)
public class Password extends ValueObject {

    // Password strength requirements
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

    @Getter
    private final String value;

    @Getter
    private final boolean isHashed;

    private Password(String value, boolean isHashed) {
        this.value = value;
        this.isHashed = isHashed;
        validate();
    }

    /**
     * Create password from raw text (will be validated for strength)
     */
    public static Password ofRaw(String rawPassword) {
        return new Password(rawPassword, false);
    }

    /**
     * Create password from already hashed value (no strength validation)
     */
    public static Password ofHashed(String hashedPassword) {
        return new Password(hashedPassword, true);
    }

    @Override
    protected void validate() {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        if (isHashed) {
            // For hashed passwords, just check it's not empty and reasonable length
            if (value.length() < 10 || value.length() > 255) {
                throw new IllegalArgumentException("Invalid hashed password format");
            }
        } else {
            // Raw password strength validation
            validatePasswordStrength();
        }
    }

    private void validatePasswordStrength() {
        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_LENGTH + " characters long");
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Password cannot exceed " + MAX_LENGTH + " characters");
        }

        if (!UPPERCASE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }

        if (!LOWERCASE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }

        if (!DIGIT_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }

    /**
     * Check if password meets minimum requirements (for existing users with weak passwords)
     */
    public boolean meetsMinimumRequirements() {
        return !isHashed && value.length() >= 6; // Legacy support
    }

    @Override
    public String toString() {
        return isHashed ? "[HASHED]" : "[RAW]";
    }
}