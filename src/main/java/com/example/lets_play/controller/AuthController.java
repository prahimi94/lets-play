package com.example.lets_play.controller;

import com.example.lets_play.model.User;
import com.example.lets_play.service.UserService;
import com.example.lets_play.service.ValidationService;
import com.example.lets_play.dto.LoginUserRequest;
import com.example.lets_play.dto.RegisterUserRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ValidationService validationService;


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
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        // Additional custom validation
        validationService.validateRegisterUserRequest(registerUserRequest);
        
        // Only allow if no admin exists yet (for initial setup)
        if (userService.hasAdminUser()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin already exists");
        }
        
        String token = userService.registerUser(registerUserRequest.getName(), registerUserRequest.getEmail(), registerUserRequest.getPassword(), "ADMIN");
        return ResponseEntity.ok(token);
    }
}
