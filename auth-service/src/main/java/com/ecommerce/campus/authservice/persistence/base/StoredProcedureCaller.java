package com.ecommerce.campus.authservice.persistence.base;

public interface StoredProcedureCaller {
    <T> StoredProcedureBuilder<T> callProcedure(String procedureName, Class<T> resultType);
}