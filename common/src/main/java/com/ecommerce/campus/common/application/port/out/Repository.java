package com.ecommerce.campus.common.application.port.out;

import com.ecommerce.campus.common.domain.model.AggregateRoot;
import java.util.Optional;
import java.util.List;

/**
 * Generic repository interface for aggregate roots.
 * This is an output port that will be implemented by infrastructure.
 */
public interface Repository<T extends AggregateRoot<ID>, ID> {

    /**
     * Save an aggregate root.
     */
    T save(T aggregate);

    /**
     * Find aggregate by ID.
     */
    Optional<T> findById(ID id);

    /**
     * Check if aggregate exists by ID.
     */
    boolean existsById(ID id);

    /**
     * Delete aggregate by ID.
     */
    void deleteById(ID id);

    /**
     * Find all aggregates.
     */
    List<T> findAll();

    /**
     * Count total aggregates.
     */
    long count();
}