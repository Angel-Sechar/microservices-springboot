package com.ecommerce.campus.authservice.infrastructure.web.controller;

import com.ecommerce.campus.authservice.application.port.in.usecase.ChangePasswordUseCase;
import com.ecommerce.campus.authservice.application.port.in.usecase.GetUserProfileUseCase;
import com.ecommerce.campus.authservice.application.port.in.usecase.UpdateUserProfileUseCase;
import com.ecommerce.campus.authservice.infrastructure.web.dto.request.ChangePasswordRequest;
import com.ecommerce.campus.authservice.infrastructure.web.dto.request.UpdateProfileRequest;
import com.ecommerce.campus.authservice.infrastructure.web.dto.response.ApiResponse;
import com.ecommerce.campus.authservice.infrastructure.web.dto.response.UserResponse;
import com.ecommerce.campus.authservice.infrastructure.security.CurrentUser;
import com.ecommerce.campus.authservice.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for User Management operations
 * Handles user profile management and account operations
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "User Management", description = "User profile and account management operations")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final WebMapper webMapper;

    @Operation(
            summary = "Get current user profile",
            description = "Retrieves the profile information of the currently authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserProfile(
            @CurrentUser UserPrincipal userPrincipal) {

        log.info("Get profile request for user: {}", userPrincipal.getUserId());

        // Create command with current user ID
        var command = webMapper.toGetUserProfileCommand(userPrincipal.getUserId());

        // Execute use case
        var userResponse = getUserProfileUseCase.execute(command);

        // Map application response to web response
        var webUserResponse = webMapper.toWebUserResponse(userResponse);

        var apiResponse = ApiResponse.success(
                webUserResponse,
                "User profile retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "Update user profile",
            description = "Updates the profile information of the currently authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated"
            )
    })
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserProfile(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateProfileRequest request) {

        log.info("Update profile request for user: {}", userPrincipal.getUserId());

        // Map web request to application command
        var command = webMapper.toUpdateUserProfileCommand(userPrincipal.getUserId(), request);

        // Execute use case
        var userResponse = updateUserProfileUseCase.execute(command);

        // Map application response to web response
        var webUserResponse = webMapper.toWebUserResponse(userResponse);

        var apiResponse = ApiResponse.success(
                webUserResponse,
                "Profile updated successfully"
        );

        log.info("Profile updated successfully for user: {}", userPrincipal.getUserId());
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "Change user password",
            description = "Changes the password for the currently authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or current password incorrect"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated"
            )
    })
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody ChangePasswordRequest request) {

        log.info("Change password request for user: {}", userPrincipal.getUserId());

        // Map web request to application command
        var command = webMapper.toChangePasswordCommand(userPrincipal.getUserId(), request);

        // Execute use case
        changePasswordUseCase.execute(command);

        var apiResponse = ApiResponse.<Void>success(
                null,
                "Password changed successfully"
        );

        log.info("Password changed successfully for user: {}", userPrincipal.getUserId());
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "Get user by ID (Admin only)",
            description = "Retrieves user profile by ID (requires admin privileges)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied - Admin privileges required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable String userId) {

        log.info("Get user request for ID: {}", userId);

        // Create command
        var command = webMapper.toGetUserProfileCommand(userId);

        // Execute use case
        var userResponse = getUserProfileUseCase.execute(command);

        // Map application response to web response
        var webUserResponse = webMapper.toWebUserResponse(userResponse);

        var apiResponse = ApiResponse.success(
                webUserResponse,
                "User profile retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "Deactivate user account",
            description = "Deactivates the current user's account (soft delete)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Account deactivated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated"
            )
    })
    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount(
            @CurrentUser UserPrincipal userPrincipal) {

        log.info("Deactivate account request for user: {}", userPrincipal.getUserId());

        // TODO: Implement deactivate account use case
        // For now, return success message

        var apiResponse = ApiResponse.<Void>success(
                null,
                "Account deactivation request received (feature coming soon)"
        );

        return ResponseEntity.ok(apiResponse);
    }
}