package com.ecommerce.campus.common.application.port.in;

/**
 * Marker interface for all use cases (input ports).
 * Represents business operations that can be performed.
 */
public interface UseCase<INPUT, OUTPUT> {

    /**
     * Execute the use case with given input.
     *
     * @param input the input for the use case
     * @return the output of the use case execution
     */
    OUTPUT execute(INPUT input);
}