package com.ecommerce.campus.authservice.infrastructure.external.config;

import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;

/**
 * Configuration for external authentication provider clients
 */
public class ExternalAuthConfig {

    @Bean
    public Request.Options externalRequestOptions() {
        return new Request.Options(
                3000,   // connect timeout (3 seconds - faster for external)
                5000    // read timeout (5 seconds)
        );
    }

    @Bean
    public Retryer externalRetryer() {
        return new Retryer.Default(
                500,    // initial retry interval (0.5 seconds)
                2000,   // max retry interval (2 seconds)
                2       // max attempts (less retries for external)
        );
    }
}
