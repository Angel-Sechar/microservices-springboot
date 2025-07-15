package com.ecommerce.campus.authservice.application.mapper;

import com.ecommerce.campus.authservice.domain.model.aggregate.User;
import com.ecommerce.campus.authservice.domain.model.valueobject.*;
import com.ecommerce.campus.authservice.application.dto.command.RegisterUserCommand;
import com.ecommerce.campus.authservice.application.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for User domain ↔ DTO conversions
 * Handles complex mapping between domain objects and DTOs
 */
@Mapper(componentModel = "spring")
public interface AuthServiceMapper {

    /**
     * Map User aggregate to UserResponse DTO
     */
    @Mapping(source = "id", target = "userId", qualifiedByName = "userIdToString")
    @Mapping(source = "email", target = "email", qualifiedByName = "emailToString")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    @Mapping(target = "canLogin", expression = "java(user.canLogin())")
    UserResponse toResponse(User user);

    /**
     * Map RegisterUserCommand to domain value objects for User creation
     */
    default User toDomain(RegisterUserCommand command) {
        Email email = Email.of(command.getEmail());
        Password password = Password.ofRaw(command.getPassword());
        UserRole role = command.getRole() != null ?
                UserRole.of(command.getRole()) : UserRole.USER;

        return User.create(email, password, role,
                command.getFirstName(), command.getLastName());
    }

    // Helper mapping methods

    @Named("userIdToString")
    default String userIdToString(UserId userId) {
        return userId != null ? userId.toString() : null;
    }

    @Named("emailToString")
    default String emailToString(Email email) {
        return email != null ? email.getValue() : null;
    }

    @Named("roleToString")
    default String roleToString(UserRole role) {
        return role != null ? role.getCode() : null;
    }

    @Named("statusToString")
    default String statusToString(UserStatus status) {
        return status != null ? status.getCode() : null;
    }
}