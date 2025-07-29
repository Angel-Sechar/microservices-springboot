package com.ecommerce.campus.authservice.persistence.base;

import jakarta.persistence.*;
import java.util.*;

public class StoredProcedureBuilder<T> {

    private final EntityManager entityManager;
    private final String procedureName;
    private final Class<T> resultType;
    private boolean expectsListResult = false;

    private static class Param {
        Object value;
        ParameterMode mode;
        Class<?> type;

        Param(Object value, ParameterMode mode, Class<?> type) {
            this.value = value;
            this.mode = mode;
            this.type = type;
        }
    }

    private final Map<String, Param> parameters = new LinkedHashMap<>();

    public StoredProcedureBuilder(EntityManager entityManager, String procedureName, Class<T> resultType) {
        this.entityManager = entityManager;
        this.procedureName = procedureName;
        this.resultType = resultType;
    }

    public StoredProcedureBuilder<T> mapInParam(String name, Object value) {
        parameters.put(name, new Param(value, ParameterMode.IN, value != null ? value.getClass() : Object.class));
        return this;
    }

    public StoredProcedureBuilder<T> mapOutParam(String name, Class<?> type) {
        parameters.put(name, new Param(null, ParameterMode.OUT, type));
        return this;
    }

    public StoredProcedureBuilder<T> mapInOutParam(String name, Object value, Class<?> type) {
        parameters.put(name, new Param(value, ParameterMode.INOUT, type));
        return this;
    }

    public StoredProcedureBuilder<T> expectListResult() {
        this.expectsListResult = true;
        return this;
    }

    @SuppressWarnings("unchecked") //WHEN THE COMPILER IS UNSURE
    public Object execute() {
        StoredProcedureQuery query;

        if (expectsListResult) {
            query = entityManager.createStoredProcedureQuery(procedureName, resultType);
        } else {
            query = entityManager.createStoredProcedureQuery(procedureName);
        }

        for (Map.Entry<String, Param> entry : parameters.entrySet()) {
            String name = entry.getKey();
            Param param = entry.getValue();

            query.registerStoredProcedureParameter(name, param.type, param.mode);

            if (param.mode == ParameterMode.IN || param.mode == ParameterMode.INOUT) {
                query.setParameter(name, param.value);
            }
        }

        query.execute();

        // OUT and INOUT return values
        Map<String, Object> outParams = new HashMap<>();
        for (Map.Entry<String, Param> entry : parameters.entrySet()) {
            if (entry.getValue().mode == ParameterMode.OUT || entry.getValue().mode == ParameterMode.INOUT) {
                outParams.put(entry.getKey(), query.getOutputParameterValue(entry.getKey()));
            }
        }

        if (!outParams.isEmpty()) {
            return outParams;
        }

        if (resultType == Void.class) {
            return null;
        }

        return expectsListResult ? query.getResultList() : query.getSingleResult();
    }
}
