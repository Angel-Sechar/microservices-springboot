package com.ecommerce.campus.common.domain.model;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for all domain events.
 * Events represent something important that happened in the domain.
 */
@Getter
public abstract class DomainEvent {

    private final String eventId;
    private final LocalDateTime occurredOn;
    private final String eventType;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.eventType = this.getClass().getSimpleName();
    }

    protected DomainEvent(String eventId, LocalDateTime occurredOn) {
        this.eventId = eventId;
        this.occurredOn = occurredOn;
        this.eventType = this.getClass().getSimpleName();
    }

    /**
     * Get the aggregate ID that generated this event.
     */
    public abstract String getAggregateId();

    /**
     * Get the aggregate type that generated this event.
     */
    public abstract String getAggregateType();
}