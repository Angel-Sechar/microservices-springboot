package com.ecommerce.campus.authservice.domain.exception;

import java.time.LocalDateTime;

public class AccountLockedException extends AuthenticationDomainException {

  public AccountLockedException() {
    super("Account is locked due to too many failed login attempts");
  }

  public AccountLockedException(LocalDateTime lockedUntil) {
    super("Account is locked until " + lockedUntil);
  }
}