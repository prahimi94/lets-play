package com.example.lets_play.controller;

import com.example.lets_play.service.UserService;
import com.example.lets_play.model.User;
import com.example.lets_play.dto.RegisterUserRequest;
import com.example.lets_play.dto.UpdateUserRequest;
import com.example.lets_play.dto.UpdateUserPasswordRequest;

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
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> create(
            @RequestBody RegisterUserRequest registerUserRequest) {

        User createdUser = userService.registerUser(registerUserRequest.getName(), registerUserRequest.getEmail(), registerUserRequest.getPassword(), "USER");
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> update(@PathVariable String id, 
            @RequestBody UpdateUserRequest request,
            @RequestHeader("Authorization") String authHeader) {

        User updatedUser = userService.updateUser(
            id,
            request.getName()
        );

        return ResponseEntity.ok(updatedUser);
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
}

