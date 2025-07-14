package com.ecommerce.campus.authservice.domain.model.valueobject;

import com.ecommerce.campus.common.domain.model.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UserRole value object representing user permissions
 * Extensible enum pattern for type safety
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class UserRole extends ValueObject {

    // Predefined roles
    public static final UserRole ADMIN = new UserRole("ADMIN", "System Administrator");
    public static final UserRole USER = new UserRole("USER", "Regular User");
    public static final UserRole MODERATOR = new UserRole("MODERATOR", "Content Moderator");
    public static final UserRole GUEST = new UserRole("GUEST", "Guest User");

    private static final Set<UserRole> PREDEFINED_ROLES = Set.of(ADMIN, USER, MODERATOR, GUEST);

    private final String code;
    private final String description;

    private UserRole(String code, String description) {
        this.code = code.toUpperCase().trim();
        this.description = description.trim();
        validate();
    }

    public static UserRole of(String code) {
        return PREDEFINED_ROLES.stream()
                .filter(role -> role.code.equals(code.toUpperCase().trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + code));
    }

    public static UserRole custom(String code, String description) {
        return new UserRole(code, description);
    }

    public static Set<String> getAllRoleCodes() {
        return PREDEFINED_ROLES.stream()
                .map(UserRole::getCode)
                .collect(Collectors.toSet());
    }

    @Override
    protected void validate() {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Role code cannot be null or empty");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Role description cannot be null or empty");
        }

        if (code.length() > 50) {
            throw new IllegalArgumentException("Role code cannot exceed 50 characters");
        }

        if (!code.matches("^[A-Z_]+$")) {
            throw new IllegalArgumentException("Role code must contain only uppercase letters and underscores");
        }
    }

    public boolean isAdmin() {
        return this.equals(ADMIN);
    }

    public boolean isUser() {
        return this.equals(USER);
    }

    public boolean hasElevatedPrivileges() {
        return this.equals(ADMIN) || this.equals(MODERATOR);
    }

    @Override
    public String toString() {
        return code;
    }
}
