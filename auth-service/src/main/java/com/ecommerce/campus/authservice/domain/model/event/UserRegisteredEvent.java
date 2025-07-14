package com.ecommerce.campus.authservice.domain.model.event;

import com.ecommerce.campus.common.domain.model.DomainEvent;
import com.ecommerce.campus.authservice.domain.model.valueobject.Email;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserId;
import com.ecommerce.campus.authservice.domain.model.valueobject.UserRole;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Domain event fired when a new user is registered
 */
@Getter
public class UserRegisteredEvent extends DomainEvent {

    private final UserId userId;
    private final Email email;
    private final UserRole role;
    private final LocalDateTime registeredAt;

    public UserRegisteredEvent(UserId userId, Email email, UserRole role) {
        super();
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.registeredAt = LocalDateTime.now();
    }

    @Override
    public String getEventType() {
        return "USER_REGISTERED";
    }
}
