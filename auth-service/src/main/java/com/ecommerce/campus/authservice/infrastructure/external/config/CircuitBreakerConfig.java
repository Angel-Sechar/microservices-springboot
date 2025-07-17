@Configuration
public class CircuitBreakerConfig {

    @Bean
    public CircuitBreaker notificationCircuitBreaker() {
        return CircuitBreaker.ofDefaults("notification-service");
    }

    @Bean
    public CircuitBreaker profileCircuitBreaker() {
        return CircuitBreaker.ofDefaults("user-profile-service");
    }
}