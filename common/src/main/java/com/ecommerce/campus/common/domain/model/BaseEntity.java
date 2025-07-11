package com.ecommerce.campus.common.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for all domain entities.
 * Follows DDD principles with identity and domain events.
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity<ID> {

    @EqualsAndHashCode.Include
    protected ID id;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected BaseEntity() {
        // Default constructor for JPA
    }

    protected BaseEntity(ID id) {
        this.id = id;
    }

    /**
     * Add a domain event to be published.
     */
    protected void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    /**
     * Get all domain events and clear the list.
     */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    /**
     * Get domain events without clearing.
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    /**
     * Clear all domain events.
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    /**
     * Check if entity has domain events.
     */
    public boolean hasDomainEvents() {
        return !this.domainEvents.isEmpty();
    }
}