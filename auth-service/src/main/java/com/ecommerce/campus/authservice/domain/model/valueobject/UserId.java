package com.ecommerce.campus.authservice.domain.model.valueobject;

import com.ecommerce.campus.common.domain.model.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * UserId value object - unique identifier for users
 * Immutable and self-validating
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class UserId extends ValueObject {

    private final UUID value;

    private UserId(UUID value) {
        this.value = value;
        validate();
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }

    public static UserId of(String value) {
        try {
            return new UserId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + value);
        }
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    @Override
    protected void validate() {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
