package com.example.lets_play.controller;

import com.example.lets_play.service.UserService;
import com.example.lets_play.model.User;
import com.example.lets_play.dto.RegisterUserRequest;
import com.example.lets_play.dto.UpdateUserRequest;
import com.example.lets_play.dto.UpdateUserPasswordRequest;
import com.example.lets_play.dto.UserProfileResponse;
import com.example.lets_play.dto.UserResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll() {
        return userService.getAllUsers().stream()
                .map(UserResponse::fromUser)
                .collect(java.util.stream.Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> create(
            @RequestBody RegisterUserRequest registerUserRequest,
            @RequestParam(defaultValue = "USER") String role) {

        // Only allow ADMIN or USER roles
        if (!role.equals("ADMIN") && !role.equals("USER")) {
            role = "USER";
        }

        User createdUser = userService.createUser(registerUserRequest.getName(), registerUserRequest.getEmail(), registerUserRequest.getPassword(), role);
        return ResponseEntity.ok(UserResponse.fromUser(createdUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> update(@PathVariable String id, 
            @RequestBody UpdateUserRequest request,
            @RequestHeader("Authorization") String authHeader) {

        User updatedUser = userService.updateUser(
            id,
            request.getName()
        );

        return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
    }

    @PutMapping("/updatePassword")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updatePassword(
            @RequestBody UpdateUserPasswordRequest updateUserPasswordRequest,
            @RequestHeader("Authorization") String authHeader) {

        User user = userService.getUserFromToken(authHeader);
        userService.updatePassword(
                user.getId(),
                updateUserPasswordRequest.getOldPassword(),
                updateUserPasswordRequest.getNewPassword()
        );

        return ResponseEntity.ok("Password updated successfully.");
    }

        @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            User user = userService.getUserFromToken(authHeader);
            UserProfileResponse response = new UserProfileResponse(
                user.getName(),
                user.getEmail(),
                user.getRole()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error in getCurrentUser: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to be handled by GlobalExceptionHandler
        }
    }

    @GetMapping("/debug-auth")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> debugAuth() {
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        StringBuilder debug = new StringBuilder();
        debug.append("User: ").append(auth.getName()).append("\n");
        debug.append("Authorities: ").append(auth.getAuthorities()).append("\n");
        debug.append("Is Admin: ").append(auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))).append("\n");
        
        return ResponseEntity.ok(debug.toString());
    }

    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("✅ You are an admin!");
    }

    @GetMapping("/admin-test")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> adminTest() {
        return ResponseEntity.ok("✅ hasAuthority('ROLE_ADMIN') works!");
    }
}

