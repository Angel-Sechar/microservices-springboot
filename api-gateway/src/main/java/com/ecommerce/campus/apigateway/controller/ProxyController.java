package com.ecommerce.campus.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/")
public class ProxyController {

    @GetMapping
    public Mono<Map<String, String>> root() {
        return Mono.just(Map.of(
                "message", "Campus E-commerce API Gateway",
                "version", "1.0.0",
                "endpoints", "/api/auth/**, /api/gateway/**"
        ));
    }

    @GetMapping("/status")
    public Mono<Map<String, String>> status() {
        return Mono.just(Map.of(
                "gateway", "RUNNING",
                "auth-service", "CONNECTED"
        ));
    }
}