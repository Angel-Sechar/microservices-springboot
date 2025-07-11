package com.ecommerce.campus.common.application.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * Standard pagination response.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageResponse<T> extends BaseResponse {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean empty;

    public PageResponse() {
        super();
    }

    public PageResponse(List<T> content, int page, int size, long totalElements) {
        super();
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.first = page == 0;
        this.last = page >= totalPages - 1;
        this.empty = content == null || content.isEmpty();
    }
}