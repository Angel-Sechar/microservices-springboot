package com.ecommerce.campus.authservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.ecommerce.campus.authservice.persistence.jpa"
)
@EnableRedisRepositories(
        basePackages = "com.ecommerce.campus.authservice.persistence.redis"
)
@EnableTransactionManagement
public class PersistenceConfig {

}