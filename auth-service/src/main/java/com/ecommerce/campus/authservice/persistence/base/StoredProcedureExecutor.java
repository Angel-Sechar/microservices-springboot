package com.ecommerce.campus.authservice.persistence.base;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component
public class StoredProcedureExecutor implements StoredProcedureCaller {

    private final EntityManager entityManager;

    public StoredProcedureExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public <T> StoredProcedureBuilder<T> callProcedure(String procedureName, Class<T> resultType) {
        return new StoredProcedureBuilder<>(entityManager, procedureName, resultType);
    }
}