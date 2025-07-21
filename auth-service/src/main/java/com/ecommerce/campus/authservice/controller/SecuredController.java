package com.ecommerce.campus.authservice.controller;

import com.ecommerce.campus.authservice.dto.UserResponse;
import com.ecommerce.campus.authservice.repository.UserRepository;
import com.ecommerce.campus.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SecuredController {

    private final UserService userService;

    // Only authenticated users can access
    @GetMapping("/profile")
    public UserResponse getProfile(@AuthenticationPrincipal Long userId) {
        // userId is automatically injected from JWT token
        return userService.findById(userId);
    }

//    // Only users with ADMIN role can access
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/admin/users")
//    public List<UserResponse> getAllUsers() {
//        return userService.getAllUsers();
//    }

//    // User can only update their own profile
//    @PreAuthorize("#userId == authentication.principal")
//    @PutMapping("/user/{userId}")
//    public UserResponse updateUser(@PathVariable Long userId,
//                                   @RequestBody UpdateUserRequest request) {
//        return userService.updateUser(userId, request);
//    }

//    // Multiple roles allowed
//    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
//    @DeleteMapping("/user/{userId}")
//    public void deleteUser(@PathVariable Long userId) {
//        userService.deleteUser(userId);
//    }

}