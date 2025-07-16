package com.ecommerce.campus.authservice.infrastructure.security;

import com.ecommerce.campus.authservice.application.port.out.security.PasswordEncoder;
import com.ecommerce.campus.authservice.domain.model.valueobject.Password;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BCrypt implementation of PasswordEncoder port
 * Uses Spring Security's BCryptPasswordEncoder
 */
@Slf4j
@Component
public class BcryptPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bcrypt;

    public BcryptPasswordEncoder() {
        // Use strength 12 for good security/performance balance
        this.bcrypt = new BCryptPasswordEncoder(12);
        log.info("BCrypt password encoder initialized with strength 12");
    }

    @Override
    public Password encode(Password rawPassword) {
        if (rawPassword.isHashed()) {
            throw new IllegalArgumentException("Cannot encode already hashed password");
        }

        log.debug("Encoding password");
        String hashedValue = bcrypt.encode(rawPassword.getValue());

        return Password.ofHashed(hashedValue);
    }

    @Override
    public boolean matches(Password rawPassword, Password encodedPassword) {
        if (rawPassword.isHashed()) {
            throw new IllegalArgumentException("Raw password should not be hashed");
        }

        if (!encodedPassword.isHashed()) {
            throw new IllegalArgumentException("Encoded password should be hashed");
        }

        boolean matches = bcrypt.matches(rawPassword.getValue(), encodedPassword.getValue());
        log.debug("Password match result: {}", matches);

        return matches;
    }

    @Override
    public boolean needsReEncoding(Password encodedPassword) {
        if (!encodedPassword.isHashed()) {
            throw new IllegalArgumentException("Password should be hashed for re-encoding check");
        }

        // BCrypt handles version checks internally
        boolean needsReEncoding = bcrypt.upgradeEncoding(encodedPassword.getValue());

        if (needsReEncoding) {
            log.info("Password needs re-encoding for security upgrade");
        }

        return needsReEncoding;
    }
}