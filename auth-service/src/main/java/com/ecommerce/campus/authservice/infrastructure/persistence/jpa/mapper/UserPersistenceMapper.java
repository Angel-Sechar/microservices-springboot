package com.ecommerce.campus.authservice.infrastructure.persistence.jpa.mapper;

import com.ecommerce.campus.authservice.domain.model.aggregate.User;
import com.ecommerce.campus.authservice.domain.model.valueobject.*;
import com.ecommerce.campus.authservice.infrastructure.persistence.jpa.entity.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for JPA Entity ↔ Domain conversion
 */
@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    /**
     * Convert JPA entity to domain object
     */
    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToUserId")
    @Mapping(source = "email", target = "email", qualifiedByName = "stringToEmail")
    @Mapping(source = "passwordHash", target = "password", qualifiedByName = "stringToHashedPassword")
    @Mapping(source = "role", target = "role", qualifiedByName = "enumToUserRole")
    @Mapping(source = "status", target = "status", qualifiedByName = "enumToUserStatus")
    User toDomain(UserJpaEntity entity);

    /**
     * Convert domain object to JPA entity
     */
    @Mapping(source = "id", target = "id", qualifiedByName = "userIdToUuid")
    @Mapping(source = "email", target = "email", qualifiedByName = "emailToString")
    @Mapping(source = "password", target = "passwordHash", qualifiedByName = "passwordToString")
    @Mapping(source = "role", target = "role", qualifiedByName = "userRoleToEnum")
    @Mapping(source = "status", target = "status", qualifiedByName = "userStatusToEnum")
    UserJpaEntity toEntity(User domain);

    // Mapping helper methods

    @Named("uuidToUserId")
    default UserId uuidToUserId(java.util.UUID uuid) {
        return uuid != null ? UserId.of(uuid) : null;
    }

    @Named("userIdToUuid")
    default java.util.UUID userIdToUuid(UserId userId) {
        return userId != null ? userId.getValue() : null;
    }

    @Named("stringToEmail")
    default Email stringToEmail(String email) {
        return email != null ? Email.of(email) : null;
    }

    @Named("emailToString")
    default String emailToString(Email email) {
        return email != null ? email.getValue() : null;
    }

    @Named("stringToHashedPassword")
    default Password stringToHashedPassword(String passwordHash) {
        return passwordHash != null ? Password.ofHashed(passwordHash) : null;
    }

    @Named("passwordToString")
    default String passwordToString(Password password) {
        return password != null ? password.getValue() : null;
    }

    @Named("enumToUserRole")
    default UserRole enumToUserRole(UserJpaEntity.UserRoleEnum roleEnum) {
        return roleEnum != null ? UserRole.of(roleEnum.name()) : null;
    }

    @Named("userRoleToEnum")
    default UserJpaEntity.UserRoleEnum userRoleToEnum(UserRole role) {
        return role != null ? UserJpaEntity.UserRoleEnum.valueOf(role.getCode()) : null;
    }

    @Named("enumToUserStatus")
    default UserStatus enumToUserStatus(UserJpaEntity.UserStatusEnum statusEnum) {
        return statusEnum != null ? UserStatus.of(statusEnum.name()) : null;
    }

    @Named("userStatusToEnum")
    default UserJpaEntity.UserStatusEnum userStatusToEnum(UserStatus status) {
        return status != null ? UserJpaEntity.UserStatusEnum.valueOf(status.getCode()) : null;
    }
}