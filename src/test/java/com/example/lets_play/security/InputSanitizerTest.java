package com.example.lets_play.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InputSanitizerTest {

    @Autowired
    private InputSanitizer inputSanitizer;

    @Test
    public void testMongoInjectionDetection() {
        // Test cases that should be detected as injection attempts
        String[] dangerousInputs = {
            "$where: function() { return true; }",
            "'; $gt: ''",
            "{$regex: /admin/}",
            "javascript:alert('xss')",
            "function() { return this.password; }",
            "'; db.users.drop(); //",
            "$where: this.username == 'admin'",
            "{$ne: null}",
            "eval('malicious code')"
        };

        for (String input : dangerousInputs) {
            assertTrue(inputSanitizer.containsMongoInjection(input), 
                "Should detect injection in: " + input);
        }
    }

    @Test
    public void testSafeInputs() {
        // Test cases that should be considered safe
        String[] safeInputs = {
            "iPhone 12 Pro",
            "Gaming laptop for developers",
            "user@example.com",
            "Product-Name_123",
            "Normal description text",
            "Hello World",
            "Price: 299.99"
        };

        for (String input : safeInputs) {
            assertFalse(inputSanitizer.containsMongoInjection(input), 
                "Should NOT detect injection in: " + input);
        }
    }

    @Test
    public void testInputSanitization() {
        String maliciousInput = "$where: function() { return true; }; //";
        String sanitized = inputSanitizer.sanitizeString(maliciousInput);
        
        // Should remove dangerous characters
        assertFalse(sanitized.contains("$"));
        assertFalse(sanitized.contains("{"));
        assertFalse(sanitized.contains("}"));
        assertFalse(sanitized.contains(";"));
    }

    @Test
    public void testObjectIdValidation() {
        // Valid ObjectId format
        assertTrue(inputSanitizer.isValidObjectId("507f1f77bcf86cd799439011"));
        
        // Invalid formats
        assertFalse(inputSanitizer.isValidObjectId("invalid-id"));
        assertFalse(inputSanitizer.isValidObjectId("507f1f77bcf86cd79943901")); // too short
        assertFalse(inputSanitizer.isValidObjectId("507f1f77bcf86cd7994390111")); // too long
        assertFalse(inputSanitizer.isValidObjectId("507f1f77bcf86cd79943901g")); // invalid char
    }

    @Test
    public void testEmailValidation() {
        // Valid emails
        assertTrue(inputSanitizer.isValidEmail("user@example.com"));
        assertTrue(inputSanitizer.isValidEmail("test.email+tag@domain.co.uk"));
        
        // Invalid emails
        assertFalse(inputSanitizer.isValidEmail("invalid-email"));
        assertFalse(inputSanitizer.isValidEmail("@domain.com"));
        assertFalse(inputSanitizer.isValidEmail("user@"));
        assertFalse(inputSanitizer.isValidEmail("user@domain"));
    }

    @Test
    public void testProductNameValidation() {
        // Valid product names
        assertTrue(inputSanitizer.isValidProductName("iPhone 12"));
        assertTrue(inputSanitizer.isValidProductName("Gaming_Laptop-Pro"));
        assertTrue(inputSanitizer.isValidProductName("Product 123"));
        
        // Invalid product names
        assertFalse(inputSanitizer.isValidProductName("$where: attack"));
        assertFalse(inputSanitizer.isValidProductName("function()"));
        assertFalse(inputSanitizer.isValidProductName("x")); // too short
        assertFalse(inputSanitizer.isValidProductName("")); // empty
    }

    @Test
    public void testSearchQuerySanitization() {
        String dangerousQuery = "$where: function() { return true; }; //comment";
        String sanitized = inputSanitizer.sanitizeSearchQuery(dangerousQuery);
        
        // Should remove dangerous parts but keep searchable content
        assertFalse(sanitized.contains("$"));
        assertFalse(sanitized.contains("{"));
        assertFalse(sanitized.contains("}"));
        assertFalse(sanitized.contains(";"));
        
        // Should keep normal search terms
        String normalQuery = "search term";
        String sanitizedNormal = inputSanitizer.sanitizeSearchQuery(normalQuery);
        assertEquals("search term", sanitizedNormal);
    }
}
