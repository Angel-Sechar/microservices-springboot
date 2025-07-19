package com.ecommerce.campus.authservice.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        Set<String> roles,
        LocalDateTime createdAt
) {
    //To return User without password. (Create a mapper in the future)
    public static UserResponse from(com.ecommerce.campus.authservice.model.User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(java.util.stream.Collectors.toSet()),
                user.getCreatedAt()
        );
    }
}