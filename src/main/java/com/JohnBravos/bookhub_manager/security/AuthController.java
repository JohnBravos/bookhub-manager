package com.JohnBravos.bookhub_manager.security;

import com.JohnBravos.bookhub_manager.dto.Request.LoginRequest;
import com.JohnBravos.bookhub_manager.dto.Request.RegisterRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ApiResponse;
import com.JohnBravos.bookhub_manager.dto.Response.LoginResponse;
import com.JohnBravos.bookhub_manager.dto.Response.RegisterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Authentication", description = "User Authentication API - Login, Register, Token Validation")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new user account with email, username, and password")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data or user already exists")
    })
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @RequestBody @Valid RegisterRequest request) {
        log.info("Register request received for: {}", request.email());
        RegisterResponse response = authenticationService.register(request);
        log.info("User registered successfully: {}", response.email());
        return ResponseEntity.ok(
                ApiResponse.success(response, "User registered successfully")
        );
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with username and password, returns JWT token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful, JWT token provided"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request) {
        log.info("Login request received for user: {}", request.username());
        LoginResponse response = authenticationService.login(request);
        if (response == null) {
            log.error("❌ AuthenticationService.login() returned null for user: {}", request.username());
        } else {
            log.info("✅ LoginResponse created for user: {}", response.username());
        }

        return ResponseEntity.ok(
                ApiResponse.success(response, "Login successful")
        );
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate JWT token", description = "Validate if the provided JWT token is still valid (requires Authorization header)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token is valid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token is invalid or expired")
    })
    public ResponseEntity<ApiResponse<Void>> validateToken() {
        log.info("Token validation request");
        return ResponseEntity.ok(ApiResponse.success("Token is valid"));
    }
}
