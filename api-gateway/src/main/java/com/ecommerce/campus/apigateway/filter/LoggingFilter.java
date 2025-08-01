package com.ecommerce.campus.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Log incoming request
        log.info("Gateway Request: {} {} from {}",
                request.getMethod(),
                request.getPath(),
                request.getRemoteAddress());

        // Continue with the request and log response
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            int statusCode = exchange.getResponse().getStatusCode().value();
            log.info("Gateway Response: {} {} - Status: {}",
                    request.getMethod(),
                    request.getPath(),
                    statusCode);
        }));
    }

    @Override
    public int getOrder() {
        return -1; // Execute first
    }
}