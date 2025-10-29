package com.example.lets_play.service;

import com.example.lets_play.security.InputSanitizer;
import com.example.lets_play.dto.ProductRequest;
import com.example.lets_play.dto.RegisterUserRequest;
import com.example.lets_play.dto.LoginUserRequest;
import com.example.lets_play.exception.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for performing custom validation and sanitization
 */
@Service
public class ValidationService {
    
    @Autowired
    private InputSanitizer inputSanitizer;
    
    /**
     * Validates and sanitizes ProductRequest
     */
    public void validateProductRequest(ProductRequest request) {
        if (request == null) {
            throw new ValidationException("Product request cannot be null");
        }
        
        // Validate name
        if (!inputSanitizer.isValidProductName(request.getName())) {
            throw new ValidationException("Invalid product name format");
        }
        
        if (inputSanitizer.containsMongoInjection(request.getName())) {
            throw new ValidationException("Product name contains invalid characters");
        }
        
        // Validate description
        if (request.getDescription() != null) {
            if (request.getDescription().length() > 500) {
                throw new ValidationException("Product description too long");
            }
            
            if (inputSanitizer.containsMongoInjection(request.getDescription())) {
                throw new ValidationException("Product description contains invalid characters");
            }
        }
        
        // Validate price
        if (!inputSanitizer.isValidNumber(request.getPrice(), 0.01, 999999.99)) {
            throw new ValidationException("Invalid product price");
        }
    }
    
    /**
     * Validates and sanitizes RegisterUserRequest
     */
    public void validateRegisterUserRequest(RegisterUserRequest request) {
        if (request == null) {
            throw new ValidationException("Registration request cannot be null");
        }
        
        // Validate name
        if (!inputSanitizer.isValidUserName(request.getName())) {
            throw new ValidationException("Invalid name format");
        }
        
        // Validate email
        if (!inputSanitizer.isValidEmail(request.getEmail())) {
            throw new ValidationException("Invalid email format");
        }
        
        if (inputSanitizer.containsMongoInjection(request.getEmail())) {
            throw new ValidationException("Email contains invalid characters");
        }

        if (inputSanitizer.containsMongoInjection(request.getPassword())) {
            throw new ValidationException("Password contains invalid characters");
        }
        
        // Validate password strength (additional check beyond annotation)
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long");
        }
        
        // Validate role
        if (request.getRole() != null && 
            !request.getRole().equals("USER") && 
            !request.getRole().equals("ADMIN")) {
            throw new ValidationException("Invalid role specified");
        }
    }
    
    /**
     * Validates and sanitizes LoginUserRequest
     */
    public void validateLoginUserRequest(LoginUserRequest request) {
        if (request == null) {
            throw new ValidationException("Login request cannot be null");
        }
        
        // Validate email
        if (!inputSanitizer.isValidEmail(request.getEmail())) {
            throw new ValidationException("Invalid email format");
        }
        
        if (inputSanitizer.containsMongoInjection(request.getEmail()) ||
            inputSanitizer.containsMongoInjection(request.getPassword())) {
            throw new ValidationException("Login credentials contain invalid characters");
        }
    }
    
    /**
     * Validates MongoDB ObjectId
     */
    public void validateObjectId(String id, String fieldName) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException(fieldName + " ID cannot be empty");
        }
        
        if (!inputSanitizer.isValidObjectId(id.trim())) {
            throw new ValidationException("Invalid " + fieldName + " ID format");
        }
        
        if (inputSanitizer.containsMongoInjection(id)) {
            throw new ValidationException(fieldName + " ID contains invalid characters");
        }
    }
    
    /**
     * Validates search query
     */
    public String validateAndSanitizeSearchQuery(String query) {
        if (query == null) {
            return "";
        }
        
        String sanitized = inputSanitizer.sanitizeSearchQuery(query);
        
        if (inputSanitizer.containsMongoInjection(sanitized)) {
            throw new ValidationException("Search query contains invalid characters");
        }
        
        return sanitized;
    }
}
