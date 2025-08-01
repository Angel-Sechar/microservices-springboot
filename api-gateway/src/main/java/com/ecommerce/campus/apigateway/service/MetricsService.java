package com.ecommerce.campus.apigateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class MetricsService {

    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    private final LocalDateTime startTime = LocalDateTime.now();

    /**
     * Record a successful request
     */
    public void recordSuccessfulRequest() {
        totalRequests.incrementAndGet();
        successfulRequests.incrementAndGet();
    }

    /**
     * Record a failed request
     */
    public void recordFailedRequest() {
        totalRequests.incrementAndGet();
        failedRequests.incrementAndGet();
    }

    /**
     * Get gateway metrics
     */
    public Mono<GatewayMetrics> getMetrics() {
        return Mono.just(new GatewayMetrics(
                totalRequests.get(),
                successfulRequests.get(),
                failedRequests.get(),
                calculateSuccessRate(),
                startTime
        ));
    }

    /**
     * Reset all metrics
     */
    public Mono<Void> resetMetrics() {
        return Mono.fromRunnable(() -> {
            totalRequests.set(0);
            successfulRequests.set(0);
            failedRequests.set(0);
            log.info("Gateway metrics reset");
        });
    }

    private double calculateSuccessRate() {
        long total = totalRequests.get();
        if (total == 0) return 0.0;
        return (double) successfulRequests.get() / total * 100;
    }

    /**
     * Gateway metrics DTO
     */
    public record GatewayMetrics(
            long totalRequests,
            long successfulRequests,
            long failedRequests,
            double successRate,
            LocalDateTime startTime
    ) {}
}