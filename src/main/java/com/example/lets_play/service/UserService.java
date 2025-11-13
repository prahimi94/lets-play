package com.example.lets_play.service;

import com.example.lets_play.exception.ResourceNotFoundException;
import com.example.lets_play.model.User;
import com.example.lets_play.repository.UserRepository;
import com.example.lets_play.repository.ProductRepository;
import com.example.lets_play.security.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String query) {
        return userRepository.findById(query);
    }

    public Optional<User> getUserByEmail(String query) {
        return userRepository.findByEmail(query);
    }

    public String registerUser(String name, String email, String rawPassword, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        String hashed = passwordEncoder.encode(rawPassword);
        User user = new User(name, email, hashed, role);
        userRepository.save(user);
        return jwtService.generateToken(user.getEmail(), user.getRole());
    }

    public User createUser(String name, String email, String rawPassword, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        String hashed = passwordEncoder.encode(rawPassword);
        User user = new User(name, email, hashed, role);
        return userRepository.save(user);
    }

    public String login(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (!userOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        User user = userOpt.get();
        boolean isPasswordCorrect = checkPassword(user.getId(), rawPassword);
        if (!isPasswordCorrect) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "password is incorrect.");
        }

        return jwtService.generateToken(user.getEmail(), user.getRole());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserFromToken(String token) {
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(String userId, String name) {
        return userRepository.findById(userId).map(u -> {
            u.setName(name);
            return userRepository.save(u);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public void updatePassword(String userId, String oldPassword, String newPassword) {
        // Check if old password is correct
        System.out.println(oldPassword);
        boolean isOldPasswordCorrect = checkPassword(userId, oldPassword);
        if (!isOldPasswordCorrect) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Old password is incorrect.");
        }

        // Check if new password is same as old password
        if (oldPassword.equals(newPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different from old password.");
        }
       
        userRepository.findById(userId).map(u -> {
            u.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(u);
            return true; // or return success boolean
        }).orElseThrow(() -> new ResourceNotFoundException("User not found2"));        
    }

    public boolean checkPassword(String userId, String rawPassword) {
        return userRepository.findById(userId)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }

    public boolean hasAdminUser() {
        return userRepository.findAll().stream()
                .anyMatch(user -> "ADMIN".equals(user.getRole()));
    }

    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Prevent deletion of the last admin
        if ("ADMIN".equals(user.getRole())) {
            long adminCount = userRepository.findAll().stream()
                    .filter(u -> "ADMIN".equals(u.getRole()))
                    .count();
            
            if (adminCount <= 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Cannot delete the last admin user");
            }
        }
        
        // Delete all products owned by this user
        // This prevents orphaned products and potential security issues
        long deletedProductsCount = productRepository.deleteByUserId(userId);
        
        if (deletedProductsCount > 0) {
            System.out.println("Deleted " + deletedProductsCount + " products owned by user: " + user.getEmail());
        }
        
        userRepository.deleteById(userId);
    }

}
