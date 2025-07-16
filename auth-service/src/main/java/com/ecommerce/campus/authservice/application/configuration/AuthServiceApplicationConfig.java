package com.ecommerce.campus.authservice.application.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Application layer configuration
 * Enables component scanning for application services
 */
@Configuration
@ComponentScan(basePackages = {
        "com.ecommerce.campus.auth.application.service",
        "com.ecommerce.campus.auth.application.mapper"
})
public class AuthServiceApplicationConfig {

    // Additional application-level configurations can be added here
    // For example: validation configuration, event handling, etc.
}