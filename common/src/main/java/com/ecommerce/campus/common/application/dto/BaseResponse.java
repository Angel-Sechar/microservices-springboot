package com.ecommerce.campus.common.application.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Base class for all response DTOs.
 */
@Data
public abstract class BaseResponse {

    private String responseId;
    private LocalDateTime timestamp;
    private boolean success;
    private String message;

    protected BaseResponse() {
        this.responseId = java.util.UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.success = true;
    }

    protected BaseResponse(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }

    public static <T extends BaseResponse> T success(T response) {
        response.setSuccess(true);
        return response;
    }

    public static <T extends BaseResponse> T failure(T response, String message) {
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}