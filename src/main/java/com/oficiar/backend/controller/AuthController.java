package com.oficiar.backend.controller;

import com.oficiar.backend.dto.LoginOutDTO;
import com.oficiar.backend.dto.LoginRequest;
import com.oficiar.backend.dto.RegisterRequest;
import com.oficiar.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth") // Esta ruta coincide con el permitAll() del SecurityConfig
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginOutDTO> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}