package com.ecommerce.campus.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/gateway")
public class HealthController {

    @GetMapping("/health")
    public Mono<Map<String, Object>> health() {
        return Mono.just(Map.of(
                "status", "UP",
                "service", "api-gateway",
                "timestamp", LocalDateTime.now()
        ));
    }

    @GetMapping("/info")
    public Mono<Map<String, Object>> info() {
        return Mono.just(Map.of(
                "service", "api-gateway",
                "version", "1.0.0",
                "description", "Campus E-commerce API Gateway"
        ));
    }
}