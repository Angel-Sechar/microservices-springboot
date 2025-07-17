package com.ecommerce.campus.authservice.infrastructure.external.config;

import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;

/**
 * Configuration for internal service Feign clients
 */
public class FeignClientConfig {

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                5000,   // connect timeout (5 seconds)
                10000   // read timeout (10 seconds)
        );
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(
                1000,   // initial retry interval (1 second)
                3000,   // max retry interval (3 seconds)
                3       // max attempts
        );
    }
}
