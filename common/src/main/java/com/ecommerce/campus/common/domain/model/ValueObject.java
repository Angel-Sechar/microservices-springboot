package com.ecommerce.campus.common.domain.model;

/**
 * Base class for value objects.
 * Value objects are immutable and equality is based on all attributes.
 */
public abstract class ValueObject {

    /**
     * Value objects are immutable.
     * Implement equals() and hashCode() in concrete classes.
     */
    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();

    /**
     * Template method for value object validation.
     */
    protected void validate() {
        // Override in concrete value objects
    }
}