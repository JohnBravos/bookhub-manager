package com.JohnBravos.bookhub_manager.security;

import com.JohnBravos.bookhub_manager.dto.Request.LoginRequest;
import com.JohnBravos.bookhub_manager.dto.Request.RegisterRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ApiResponse;
import com.JohnBravos.bookhub_manager.dto.Response.LoginResponse;
import com.JohnBravos.bookhub_manager.dto.Response.RegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    // ENDPOINT 1: ΕΓΓΡΑΦΗ ΝΕΟΥ ΧΡΗΣΤΗ
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("Register request received for: {}", request.email());

        RegisterResponse response = authenticationService.register(request);
        log.info("User registered successfully: {}", response.email());

        return ResponseEntity.ok(
                ApiResponse.success(response, "User registered successfully")
        );
    }

    // ENDPOINT 2: ΣΥΝΔΕΣΗ ΧΡΗΣΤΗ
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("Login request received for user: {}", request.username());

        LoginResponse response = authenticationService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Login successful")
        );
    }
}
