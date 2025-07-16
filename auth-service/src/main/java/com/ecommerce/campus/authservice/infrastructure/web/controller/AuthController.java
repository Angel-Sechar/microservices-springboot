package com.ecommerce.campus.authservice.infrastructure.web.controller;

import com.ecommerce.campus.authservice.application.port.in.usecase.LoginUserUseCase;
import com.ecommerce.campus.authservice.application.port.in.usecase.RegisterUserUseCase;
import com.ecommerce.campus.authservice.application.port.in.usecase.ValidateTokenUseCase;
import com.ecommerce.campus.authservice.infrastructure.web.dto.request.LoginRequest;
import com.ecommerce.campus.authservice.infrastructure.web.dto.request.RegisterRequest;
import com.ecommerce.campus.authservice.infrastructure.web.dto.request.TokenValidationRequest;
import com.ecommerce.campus.authservice.infrastructure.web.dto.response.ApiResponse;
import com.ecommerce.campus.authservice.infrastructure.web.dto.response.AuthResponse;
import com.ecommerce.campus.authservice.infrastructure.web.dto.response.TokenValidationResponse;
import com.ecommerce.campus.authservice.infrastructure.web.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication operations
 * Handles user registration, login, and token validation
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Authentication", description = "Authentication and authorization operations")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final ValidateTokenUseCase validateTokenUseCase;
    private final WebMapper webMapper;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with email and password"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Email already exists"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("User registration request for email: {}", request.getEmail());

        // Map web request to application command
        var command = webMapper.toRegisterUserCommand(request);

        // Execute use case
        var userResponse = registerUserUseCase.execute(command);

        // Map application response to web response
        var webUserResponse = webMapper.toWebUserResponse(userResponse);

        var apiResponse = ApiResponse.success(
                webUserResponse,
                "User registered successfully"
        );

        log.info("User registered successfully with ID: {}", webUserResponse.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates user with email and password, returns JWT tokens"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "423",
                    description = "Account locked"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        log.info("Login attempt for email: {}", request.getEmail());

        // Map web request to application command with request metadata
        var command = webMapper.toLoginCommand(request, httpRequest);

        // Execute use case
        var authenticationResponse = loginUserUseCase.execute(command);

        // Map application response to web response
        var webAuthResponse = webMapper.toWebAuthResponse(authenticationResponse);

        var apiResponse = ApiResponse.success(
                webAuthResponse,
                "Authentication successful"
        );

        log.info("Successful authentication for email: {}", request.getEmail());
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "Validate JWT token",
            description = "Validates JWT token and returns user information"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token validation result",
                    content = @Content(schema = @Schema(implementation = TokenValidationResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid token format"
            )
    })
    @PostMapping("/validate-token")
    public ResponseEntity<ApiResponse<TokenValidationResponse>> validateToken(
            @Valid @RequestBody TokenValidationRequest request) {

        log.debug("Token validation request");

        // Map web request to application command
        var command = webMapper.toValidateTokenCommand(request);

        // Execute use case
        var tokenValidationResponse = validateTokenUseCase.execute(command);

        // Map application response to web response
        var webTokenResponse = webMapper.toWebTokenValidationResponse(tokenValidationResponse);

        var apiResponse = ApiResponse.success(
                webTokenResponse,
                tokenValidationResponse.isValid() ? "Token is valid" : "Token is invalid"
        );

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "Validate JWT token from Authorization header",
            description = "Validates JWT token from Authorization header (for API Gateway)"
    )
    @GetMapping("/validate-token")
    public ResponseEntity<ApiResponse<TokenValidationResponse>> validateTokenFromHeader(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "false") boolean includeUserDetails) {

        log.debug("Token validation request from Authorization header");

        try {
            String token = extractTokenFromHeader(authHeader);

            var request = TokenValidationRequest.builder()
                    .token(token)
                    .includeUserDetails(includeUserDetails)
                    .build();

            // Delegate to POST method
            return validateToken(request);

        } catch (IllegalArgumentException e) {
            var apiResponse = ApiResponse.<TokenValidationResponse>failure(
                    "INVALID_HEADER",
                    "Invalid Authorization header format"
            );
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @Operation(
            summary = "Logout user",
            description = "Invalidates user session (placeholder for token blacklisting)"
    )
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader) {

        log.info("Logout request");

        // TODO: Implement token blacklisting
        // For now, just return success (client should discard token)

        var apiResponse = ApiResponse.<Void>success(
                null,
                "Logout successful"
        );

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "Health check endpoint",
            description = "Simple health check for the authentication service"
    )
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        var apiResponse = ApiResponse.success(
                "UP",
                "Authentication service is running"
        );
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header must start with 'Bearer '");
        }
        return authHeader.substring(7);
    }
}