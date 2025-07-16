package com.ecommerce.campus.authservice.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA configuration for auth service
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.ecommerce.campus.auth.infrastructure.persistence.jpa.repository")
@EntityScan(basePackages = "com.ecommerce.campus.auth.infrastructure.persistence.jpa.entity")
@EnableTransactionManagement
public class JpaConfig {
    // JPA configuration is handled by Spring Boot auto-configuration
    // This class provides explicit configuration if needed
}