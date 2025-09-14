package com.aslan.project.bank_rest.controller;


import com.aslan.project.bank_rest.dto.request.LoginRequest;
import com.aslan.project.bank_rest.dto.request.RefreshRequest;
import com.aslan.project.bank_rest.dto.request.RegisterRequest;
import com.aslan.project.bank_rest.dto.response.ApiResponse;
import com.aslan.project.bank_rest.dto.response.AuthResponse;
import com.aslan.project.bank_rest.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok(new ApiResponse("User registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest req) {
        return ResponseEntity.ok(authService.refresh(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshRequest req) {
        authService.logout(req);
        return ResponseEntity.ok(new ApiResponse("Logged out"));
    }
}

