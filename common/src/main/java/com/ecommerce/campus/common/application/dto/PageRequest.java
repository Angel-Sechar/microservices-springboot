package com.ecommerce.campus.common.application.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * Standard pagination request.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageRequest extends BaseRequest {

    @Min(value = 0, message = "Page number cannot be negative")
    private int page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private int size = 20;

    private String sortBy = "id";
    private String sortDirection = "ASC";

    public PageRequest() {
        super();
    }

    public PageRequest(int page, int size) {
        super();
        this.page = page;
        this.size = size;
    }

    public PageRequest(int page, int size, String sortBy, String sortDirection) {
        this(page, size);
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }
}