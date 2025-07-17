@Configuration
@EnableFeignClients(basePackages = "com.ecommerce.campus.authservice.infrastructure.external.client")
public class OpenFeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("User-Agent", "auth-service/1.0");
            requestTemplate.header("Content-Type", "application/json");
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}