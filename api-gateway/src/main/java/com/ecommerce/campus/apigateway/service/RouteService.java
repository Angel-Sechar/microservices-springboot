package com.ecommerce.campus.apigateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RouteService {

    private final RouteLocator routeLocator;

    public RouteService(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    /**
     * Get all active routes
     */
    public Flux<Route> getAllRoutes() {
        return routeLocator.getRoutes()
                .doOnNext(route -> log.debug("Active route: {} -> {}",
                        route.getId(), route.getUri()));
    }

    /**
     * Get routes as simple list for REST endpoints
     */
    public Mono<List<RouteInfo>> getRouteInfoList() {
        return getAllRoutes()
                .map(route -> new RouteInfo(
                        route.getId(),
                        route.getUri().toString(),
                        route.getPredicate().toString()
                ))
                .collectList();
    }

    /**
     * Check if a specific route exists
     */
    public Mono<Boolean> routeExists(String routeId) {
        return getAllRoutes()
                .any(route -> route.getId().equals(routeId));
    }

    /**
     * Get route count
     */
    public Mono<Long> getRouteCount() {
        return getAllRoutes().count();
    }

    /**
     * Simple route info DTO
     */
    public record RouteInfo(String id, String uri, String predicate) {}
}
