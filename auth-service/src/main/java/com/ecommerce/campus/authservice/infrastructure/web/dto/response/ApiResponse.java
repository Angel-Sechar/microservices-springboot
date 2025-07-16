package com.ecommerce.campus.authservice.infrastructure.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Generic API Response wrapper
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Response timestamp", example = "2024-01-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "Operation success status", example = "true")
    @JsonProperty("success")
    private Boolean success;

    @Schema(description = "Response message", example = "Operation completed successfully")
    @JsonProperty("message")
    private String message;

    @Schema(description = "Response data payload")
    @JsonProperty("data")
    private T data;

    @Schema(description = "Error code (present only for failures)", example = "VALIDATION_ERROR")
    @JsonProperty("errorCode")
    private String errorCode;

    @Schema(description = "Additional error details (present only for failures)")
    @JsonProperty("errors")
    private Map<String, String> errors;

    @Schema(description = "Request trace ID for debugging", example = "abc123-def456-ghi789")
    @JsonProperty("traceId")
    private String traceId;

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message(message)
                .data(data)
                .traceId(generateTraceId())
                .build();
    }

    public static <T> ApiResponse<T> failure(String errorCode, String message) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .traceId(generateTraceId())
                .build();
    }

    public static <T> ApiResponse<T> failure(String errorCode, String message, Map<String, String> errors) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .errors(errors)
                .traceId(generateTraceId())
                .build();
    }

    private static String generateTraceId() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}
