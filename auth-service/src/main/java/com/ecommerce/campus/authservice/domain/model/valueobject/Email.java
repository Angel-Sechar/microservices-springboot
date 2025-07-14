package com.ecommerce.campus.authservice.domain.model.valueobject;

import com.ecommerce.campus.common.domain.model.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

/**
 * Email value object with proper validation
 * Ensures email format compliance and business rules
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class Email extends ValueObject {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final int MAX_LENGTH = 320; // RFC 5321 limit

    private final String value;

    private Email(String value) {
        this.value = value.toLowerCase().trim(); // Normalize email
        validate();
    }

    public static Email of(String email) {
        return new Email(email);
    }

    @Override
    protected void validate() {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Email exceeds maximum length of " + MAX_LENGTH + " characters");
        }

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
    }

    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }

    public String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }

    @Override
    public String toString() {
        return value;
    }
}
