package com.ecommerce.campus.authservice.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main infrastructure configuration
 * Enables component scanning for all infrastructure components
 */
@Configuration
@ComponentScan(basePackages = {
        "com.ecommerce.campus.auth.infrastructure.persistence",
        "com.ecommerce.campus.auth.infrastructure.security",
        "com.ecommerce.campus.auth.infrastructure.messaging",
        "com.ecommerce.campus.auth.infrastructure.config"
})
public class InfrastructureConfig {
    // Infrastructure configuration
}