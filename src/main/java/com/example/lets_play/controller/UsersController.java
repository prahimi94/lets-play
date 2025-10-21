package com.example.lets_play.controller;

import com.example.lets_play.service.UserService;
import com.example.lets_play.dto.ProductRequest;
import com.example.lets_play.model.Product;
import com.example.lets_play.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    // @PostMapping
    // public ResponseEntity<User> createUser(
    //         @RequestBody UserRequest request,
    //         @RequestHeader("Authorization") String authHeader) {

    //     User user = userService.getUserFromToken(authHeader);
    //     User createdUser = userService.createUser(
    //             request.getName(),
    //             request.getEmail(),
    //             request.getPassword(),
    //             user.getId()
    //     );

    //     return ResponseEntity.ok(createdUser);
    // }

    // @PutMapping("/{id}")
    // public ResponseEntity<User> update(@PathVariable String id, 
    //         @RequestBody UserRequest request,
    //         @RequestHeader("Authorization") String authHeader) {

    //     User user = userService.getUserFromToken(authHeader);
    //     User updatedUser = userService.updateUser(
    //             id,
    //             request.getName(),
    //             request.getEmail(),
    //             request.getPassword(),
    //             user.getId()
    //     );

    //     return ResponseEntity.ok(updatedUser);
    // }

    // @DeleteMapping("/{id}")
    // public void delete(@PathVariable String id) {
    //     userService.deleteUser(id);
    // }
}

