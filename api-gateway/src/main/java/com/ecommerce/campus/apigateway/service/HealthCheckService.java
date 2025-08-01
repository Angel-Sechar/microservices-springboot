package com.ecommerce.campus.apigateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HealthCheckService {

    private final DiscoveryService discoveryService;
    private final WebClient webClient;

    public HealthCheckService(DiscoveryService discoveryService, WebClient.Builder webClientBuilder) {
        this.discoveryService = discoveryService;
        this.webClient = webClientBuilder.build();
    }

    /**
     * Check health of all registered services
     */
    public Mono<Map<String, HealthStatus>> checkAllServicesHealth() {
        return discoveryService.getRegisteredServices()
                .flatMap(services -> {
                    Map<String, HealthStatus> healthMap = new HashMap<>();

                    return Mono.just(services)
                            .flatMapIterable(serviceList -> serviceList)
                            .flatMap(service -> checkServiceHealth(service)
                                    .doOnNext(status -> healthMap.put(service, status)))
                            .then(Mono.just(healthMap));
                });
    }

    /**
     * Check health of a specific service
     */
    public Mono<HealthStatus> checkServiceHealth(String serviceId) {
        return discoveryService.getServiceInstances(serviceId)
                .flatMap(instances -> {
                    if (instances.isEmpty()) {
                        return Mono.just(new HealthStatus("DOWN", "No instances found", 0));
                    }

                    // Check first instance (simple approach)
                    ServiceInstance instance = instances.get(0);
                    String healthUrl = buildHealthUrl(instance);

                    return webClient.get()
                            .uri(healthUrl)
                            .retrieve()
                            .bodyToMono(String.class)
                            .map(response -> new HealthStatus("UP", "Service responding", instances.size()))
                            .timeout(Duration.ofSeconds(5))
                            .onErrorReturn(new HealthStatus("DOWN", "Health check failed", instances.size()));
                });
    }

    /**
     * Get gateway health summary
     */
    public Mono<GatewayHealth> getGatewayHealth() {
        return checkAllServicesHealth()
                .map(serviceHealthMap -> {
                    long upServices = serviceHealthMap.values().stream()
                            .filter(health -> "UP".equals(health.status()))
                            .count();

                    long totalServices = serviceHealthMap.size();
                    String overallStatus = upServices == totalServices ? "UP" : "DEGRADED";

                    return new GatewayHealth(
                            overallStatus,
                            totalServices,
                            upServices,
                            serviceHealthMap
                    );
                });
    }

    private String buildHealthUrl(ServiceInstance instance) {
        return String.format("http://%s:%d/actuator/health",
                instance.getHost(), instance.getPort());
    }

    /**
     * Health status DTO
     */
    public record HealthStatus(String status, String message, int instanceCount) {}

    /**
     * Gateway health DTO
     */
    public record GatewayHealth(
            String overallStatus,
            long totalServices,
            long upServices,
            Map<String, HealthStatus> serviceHealth
    ) {}
}