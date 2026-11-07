package com.cotizacion.cotizacion_backend.controller;

import com.cotizacion.cotizacion_backend.dto.LoginRequest;
import com.cotizacion.cotizacion_backend.dto.LoginResponse;
import com.cotizacion.cotizacion_backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}
