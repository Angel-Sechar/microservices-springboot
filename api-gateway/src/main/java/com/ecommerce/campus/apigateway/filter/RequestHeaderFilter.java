package com.ecommerce.campus.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-Gateway-Source", "api-gateway")
                .header("X-Request-Id", generateRequestId())
                .build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    private String generateRequestId() {
        return "req-" + System.currentTimeMillis();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}