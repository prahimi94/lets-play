package com.example.lets_play.security;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

/**
 * Utility class for sanitizing input data to prevent MongoDB injection attacks
 * and other security vulnerabilities.
 */
@Component
public class InputSanitizer {
    
    // Pattern to detect MongoDB injection attempts
    private static final Pattern MONGO_INJECTION_PATTERN = Pattern.compile(
        ".*[\\${}\\[\\]\"';\\\\].*|.*\\b(where|javascript|function|return|var|let|const|eval|setTimeout|setInterval)\\b.*",
        Pattern.CASE_INSENSITIVE
    );
    
    // Pattern for safe text (alphanumeric, spaces, basic punctuation)
    private static final Pattern SAFE_TEXT_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\-_.@!?,:;()]+$");
    
    // Pattern for email validation (additional layer beyond @Email annotation)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // Pattern for MongoDB ObjectId validation
    private static final Pattern OBJECT_ID_PATTERN = Pattern.compile("^[a-fA-F0-9]{24}$");
    
    /**
     * Sanitizes a string input by removing potentially dangerous characters
     */
    public String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove null bytes and control characters
        String sanitized = input.replaceAll("[\u0000-\u001F\u007F-\u009F]", "");
        
        // Trim whitespace
        sanitized = sanitized.trim();
        
        // Remove MongoDB-specific dangerous characters
        sanitized = sanitized.replaceAll("[\\${}\\[\\]\"';\\\\]", "");
        
        return sanitized;
    }
    
    /**
     * Checks if the input contains potential MongoDB injection patterns
     */
    public boolean containsMongoInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return MONGO_INJECTION_PATTERN.matcher(input).matches();
    }
    
    /**
     * Validates if the input is safe text (no special characters that could be dangerous)
     */
    public boolean isSafeText(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return SAFE_TEXT_PATTERN.matcher(input).matches();
    }
    
    /**
     * Validates email format (additional validation beyond @Email annotation)
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches() && email.length() <= 100;
    }
    
    /**
     * Validates MongoDB ObjectId format
     */
    public boolean isValidObjectId(String objectId) {
        if (objectId == null || objectId.isEmpty()) {
            return false;
        }
        return OBJECT_ID_PATTERN.matcher(objectId).matches();
    }
    
    /**
     * Validates numeric input (price, quantities, etc.)
     */
    public boolean isValidNumber(Double number, double min, double max) {
        if (number == null) {
            return false;
        }
        return number >= min && number <= max && !number.isInfinite() && !number.isNaN();
    }
    
    /**
     * Comprehensive input validation for product name
     */
    public boolean isValidProductName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = name.trim();
        return trimmed.length() >= 2 && 
               trimmed.length() <= 100 && 
               !containsMongoInjection(trimmed) &&
               Pattern.matches("^[a-zA-Z0-9\\s\\-_.]+$", trimmed);
    }
    
    /**
     * Comprehensive input validation for user names
     */
    public boolean isValidUserName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = name.trim();
        return trimmed.length() >= 2 && 
               trimmed.length() <= 50 && 
               !containsMongoInjection(trimmed) &&
               Pattern.matches("^[a-zA-Z\\s]+$", trimmed);
    }
    
    /**
     * Validates search query parameters
     */
    public String sanitizeSearchQuery(String query) {
        if (query == null) {
            return "";
        }
        
        // Remove dangerous characters but keep basic search functionality
        String sanitized = query.replaceAll("[\\${}\\[\\]\"';\\\\]", "");
        sanitized = sanitized.replaceAll("[\u0000-\u001F\u007F-\u009F]", "");
        sanitized = sanitized.trim();
        
        // Limit length
        if (sanitized.length() > 100) {
            sanitized = sanitized.substring(0, 100);
        }
        
        return sanitized;
    }
}
