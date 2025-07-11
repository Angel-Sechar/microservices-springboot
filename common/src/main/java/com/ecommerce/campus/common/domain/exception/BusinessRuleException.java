package com.ecommerce.campus.common.domain.exception;

/**
 * Exception thrown when business rules are violated.
 */
public class BusinessRuleException extends DomainException {

    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION");
    }

    public BusinessRuleException(String rule, String message) {
        super(String.format("Business rule '%s' violated: %s", rule, message), "BUSINESS_RULE_VIOLATION");
    }
}