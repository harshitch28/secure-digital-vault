package com.digitalvault.vault.controller;

import com.digitalvault.vault.dto.RegisterRequest;
import com.digitalvault.vault.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.digitalvault.vault.dto.LoginRequest;
import com.digitalvault.vault.dto.AuthResponse;

//@CrossOrigin()
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
        String response = authService.registerUser(request);
        if (response.equals("User registered successfully!")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.loginUser(request);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {

            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
