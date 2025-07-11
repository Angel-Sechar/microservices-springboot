package com.ecommerce.campus.common.domain.model;

public abstract class AggregateRoot<ID> extends BaseEntity<ID> {

    protected AggregateRoot() {
        super();
    }

    protected AggregateRoot(ID id) {
        super(id);
    }

    public void validate() {
        // Default implementation
    }

    public abstract boolean isValid();
}