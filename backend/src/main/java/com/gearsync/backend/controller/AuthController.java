package com.gearsync.backend.controller;

import com.gearsync.backend.dto.LoginRequest;
import com.gearsync.backend.model.User;
import com.gearsync.backend.security.JwtUtil;
import com.gearsync.backend.service.AuthService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (authService.isEmailRegistered(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }
        User saved = authService.register(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        boolean isAuthenticated = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        if (!isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        String jwtToken = jwtUtil.generateToken(loginRequest.getEmail());
        return ResponseEntity.ok(Map.of("token", jwtToken));
    }
}