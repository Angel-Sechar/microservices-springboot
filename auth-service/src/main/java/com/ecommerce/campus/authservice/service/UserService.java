package com.ecommerce.campus.authservice.service;

import com.ecommerce.campus.authservice.dto.RegisterRequest;
import com.ecommerce.campus.authservice.dto.UserResponse;
import com.ecommerce.campus.authservice.exception.AuthException;
import com.ecommerce.campus.authservice.model.Role;
import com.ecommerce.campus.authservice.model.User;
import com.ecommerce.campus.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Cacheable(value = "user", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByUsername(request.username())) {
            throw new AuthException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException("Email already exists");
        }

        // Create new user
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .paternalSurname(request.paternalSurname())
                .maternalSurname(request.maternalSurname())
                .roles(Set.of(new Role("ROLE_USER"))) // Default role
                .build();

        user = userRepository.save(user);

        log.info("New user registered: {}", user.getUsername());

        return UserResponse.from(user);
    }

    @Cacheable(value = "users", key = "#userId")
    public UserResponse findById(Long userId) {
        return UserResponse.from(userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("User not found")));
    }
}

