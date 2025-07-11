package com.ecommerce.campus.common.application.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * Base class for all request DTOs.
 */
@Data
public abstract class BaseRequest {

    @NotNull(message = "Request ID cannot be null")
    private String requestId;

    private String userId;
    private String correlationId;

    protected BaseRequest() {
        this.requestId = java.util.UUID.randomUUID().toString();
    }

    protected BaseRequest(String requestId) {
        this.requestId = requestId;
    }
}