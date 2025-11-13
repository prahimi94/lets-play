package com.example.lets_play.controller;

import com.example.lets_play.service.UserService;
import com.example.lets_play.service.ValidationService;
import com.example.lets_play.service.TokenBlacklistService;
import com.example.lets_play.dto.LoginUserRequest;
import com.example.lets_play.dto.RegisterUserRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ValidationService validationService;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;


    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        // Additional custom validation
        validationService.validateRegisterUserRequest(registerUserRequest);

        String token = userService.registerUser(registerUserRequest.getName(), registerUserRequest.getEmail(), registerUserRequest.getPassword(), "USER");
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginUserRequest loginUserRequest) {
        // Additional custom validation
        validationService.validateLoginUserRequest(loginUserRequest);
        
        String token = userService.login(loginUserRequest.getEmail(), loginUserRequest.getPassword());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        // Additional custom validation
        validationService.validateRegisterUserRequest(registerUserRequest);
        
        // Only allow if no admin exists yet (for initial setup)
        // if (userService.hasAdminUser()) {
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin already exists");
        // }
        
        String token = userService.registerUser(registerUserRequest.getName(), registerUserRequest.getEmail(), registerUserRequest.getPassword(), "ADMIN");
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            // Get user info for logging purposes
            String email = userService.getUserFromToken(authHeader).getEmail();
            
            // Blacklist the token so it cannot be used again
            tokenBlacklistService.blacklistToken(authHeader);
            
            // Log the logout event
            System.out.println("User logged out and token blacklisted: " + email);
            
            return ResponseEntity.ok("Logged out successfully. Token has been invalidated.");
            
        } catch (Exception e) {
            // Even if token parsing fails, blacklist it anyway
            tokenBlacklistService.blacklistToken(authHeader);
            return ResponseEntity.ok("Logged out successfully. Token has been invalidated.");
        }
    }

    @PostMapping("/validate-token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            // Check if token is blacklisted
            if (tokenBlacklistService.isTokenBlacklisted(authHeader)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token has been invalidated (logged out)");
            }
            
            String email = userService.getUserFromToken(authHeader).getEmail();
            return ResponseEntity.ok("Token is valid for user: " + email);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/blacklist-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getBlacklistStatus() {
        int count = tokenBlacklistService.getBlacklistedTokenCount();
        return ResponseEntity.ok("Blacklisted tokens count: " + count);
    }
}
