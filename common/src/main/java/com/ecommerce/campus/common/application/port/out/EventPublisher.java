package com.ecommerce.campus.common.application.port.out;

import com.ecommerce.campus.common.domain.model.DomainEvent;

/**
 * Port for publishing domain events.
 * This will be implemented by infrastructure layer.
 */
public interface EventPublisher {

    /**
     * Publish a single domain event.
     */
    void publish(DomainEvent event);

    /**
     * Publish multiple domain events.
     */
    void publishAll(Iterable<DomainEvent> events);
}