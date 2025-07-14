package com.ecommerce.campus.authservice.domain.model.valueobject;

import com.ecommerce.campus.common.domain.model.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * UserStatus value object representing user account state
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class UserStatus extends ValueObject {

    public static final UserStatus ACTIVE = new UserStatus("ACTIVE", "Active user account");
    public static final UserStatus INACTIVE = new UserStatus("INACTIVE", "Inactive user account");
    public static final UserStatus SUSPENDED = new UserStatus("SUSPENDED", "Suspended user account");
    public static final UserStatus PENDING_VERIFICATION = new UserStatus("PENDING_VERIFICATION", "Pending email verification");
    public static final UserStatus LOCKED = new UserStatus("LOCKED", "Locked due to security reasons");

    private final String code;
    private final String description;

    private UserStatus(String code, String description) {
        this.code = code;
        this.description = description;
        validate();
    }
/*change creating a constant about UserStatus*/
    public static UserStatus of(String code) {
        return switch (code.toUpperCase()) {
            case "ACTIVE" -> ACTIVE;
            case "INACTIVE" -> INACTIVE;
            case "SUSPENDED" -> SUSPENDED;
            case "PENDING_VERIFICATION" -> PENDING_VERIFICATION;
            case "LOCKED" -> LOCKED;
            default -> throw new IllegalArgumentException("Unknown user status: " + code);
        };
    }

    @Override
    protected void validate() {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Status code cannot be null or empty");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Status description cannot be null or empty");
        }
    }

    public boolean canLogin() {
        return this.equals(ACTIVE);
    }

    public boolean requiresVerification() {
        return this.equals(PENDING_VERIFICATION);
    }

    public boolean isBlocked() {
        return this.equals(SUSPENDED) || this.equals(LOCKED);
    }

    @Override
    public String toString() {
        return code;
    }
}