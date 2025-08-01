package com.ecommerce.campus.apigateway.controller;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/gateway")
public class RouteController {

    private final RouteLocator routeLocator;

    public RouteController(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @GetMapping("/routes")
    public Flux<Route> getRoutes() {
        return routeLocator.getRoutes();
    }
}