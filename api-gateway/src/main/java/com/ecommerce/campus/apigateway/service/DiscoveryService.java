package com.ecommerce.campus.apigateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DiscoveryService {

    private final DiscoveryClient discoveryClient;

    public DiscoveryService(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * Get all registered services
     */
    public Mono<List<String>> getRegisteredServices() {
        return Mono.fromCallable(() -> {
            List<String> services = discoveryClient.getServices();
            log.info("Found {} registered services: {}", services.size(), services);
            return services;
        });
    }

    /**
     * Get instances of a specific service
     */
    public Mono<List<ServiceInstance>> getServiceInstances(String serviceId) {
        return Mono.fromCallable(() -> {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            log.info("Found {} instances for service '{}'", instances.size(), serviceId);
            return instances;
        });
    }

    /**
     * Get service registry summary
     */
    public Mono<ServiceRegistryInfo> getServiceRegistryInfo() {
        return Mono.fromCallable(() -> {
            List<String> services = discoveryClient.getServices();
            Map<String, Integer> serviceInstanceCounts = services.stream()
                    .collect(Collectors.toMap(
                            service -> service,
                            service -> discoveryClient.getInstances(service).size()
                    ));

            return new ServiceRegistryInfo(
                    services.size(),
                    serviceInstanceCounts.values().stream().mapToInt(Integer::intValue).sum(),
                    serviceInstanceCounts
            );
        });
    }

    /**
     * Check if a service is available
     */
    public Mono<Boolean> isServiceAvailable(String serviceId) {
        return getServiceInstances(serviceId)
                .map(instances -> !instances.isEmpty());
    }

    /**
     * Service registry info DTO
     */
    public record ServiceRegistryInfo(
            int totalServices,
            int totalInstances,
            Map<String, Integer> serviceInstanceCounts
    ) {}
}
