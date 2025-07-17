package com.ecommerce.campus.authservice.infrastructure.external.client;

import com.ecommerce.campus.authservice.infrastructure.external.dto.request.EmailRequest;
import com.ecommerce.campus.authservice.infrastructure.external.dto.response.EmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for notification service communication
 */
@FeignClient(
        name = "notification-service",
        path = "/api/v1/notifications",
        configuration = FeignClientConfig.class
)
public interface NotificationFeignClient {

    @PostMapping("/email")
    EmailResponse sendEmail(@RequestBody EmailRequest request);

    @PostMapping("/sms")
    void sendSms(@RequestBody SmsRequest request);
}