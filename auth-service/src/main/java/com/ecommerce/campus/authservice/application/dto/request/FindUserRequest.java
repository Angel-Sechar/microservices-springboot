package com.ecommerce.campus.authservice.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindUserRequest {

    private String userId;    // UUID as string
    private String email;     // Alternative lookup

    // Exactly one should be provided
    public boolean isValid() {
        return (userId != null && !userId.isBlank()) ^ (email != null && !email.isBlank());
    }
}
