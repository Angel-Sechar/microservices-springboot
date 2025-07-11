package com.ecommerce.campus.common.application.port.in;

/**
 * Marker interface for all queries (read operations).
 * Separates commands (write) from queries (read) - CQRS pattern.
 */
public interface Query<INPUT, OUTPUT> {

    /**
     * Execute the query with given input.
     *
     * @param input the query parameters
     * @return the query result
     */
    OUTPUT execute(INPUT input);
}