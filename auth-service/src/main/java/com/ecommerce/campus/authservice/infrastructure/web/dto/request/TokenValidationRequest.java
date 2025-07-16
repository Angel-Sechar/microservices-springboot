package com.ecommerce.campus.authservice.infrastructure.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Web DTO for token validation requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token validation request")
public class TokenValidationRequest {

    @Schema(description = "JWT token to validate", example = "eyJhbGciOiJIUzI1NiIs...")
    @NotBlank(message = "Token is required")
    @JsonProperty("token")
    private String token;

    @Schema(description = "Include full user details in response", example = "false")
    @JsonProperty("includeUserDetails")
    private Boolean includeUserDetails = false;
}