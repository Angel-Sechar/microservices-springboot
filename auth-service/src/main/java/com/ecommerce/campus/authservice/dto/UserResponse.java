package com.ecommerce.campus.authservice.dto;

import com.ecommerce.campus.authservice.model.Role;
import com.ecommerce.campus.authservice.model.User;
import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String paternalName,
        String maternalName,
        Set<String> roles,
        LocalDateTime createdAt
) {
    //To return User without password. (Create a mapper in the future)
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getPaternalSurname(),
                user.getMaternalSurname(),
                user.getRoles().stream()
                        .map(Role::getRoleName)
                        .collect(java.util.stream.Collectors.toSet()),
                user.getCreatedAt()
        );
    }
}