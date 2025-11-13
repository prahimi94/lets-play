package com.example.lets_play.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service to manage blacklisted JWT tokens
 * In a production environment, this should use Redis or a database
 */
@Service
public class TokenBlacklistService {

    // In-memory storage (use Redis in production)
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public TokenBlacklistService() {
        // Clean up expired tokens every hour
        scheduler.scheduleAtFixedRate(this::cleanupExpiredTokens, 1, 1, TimeUnit.HOURS);
    }

    /**
     * Add token to blacklist
     */
    public void blacklistToken(String token) {
        if (token != null && !token.isEmpty()) {
            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            blacklistedTokens.add(token);
            System.out.println("Token blacklisted: " + token.substring(0, Math.min(10, token.length())) + "...");
        }
    }

    /**
     * Check if token is blacklisted
     */
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        return blacklistedTokens.contains(token);
    }

    /**
     * Clean up expired tokens (this is a simple implementation)
     * In production, you'd check token expiration dates
     */
    private void cleanupExpiredTokens() {
        // For now, clear all tokens older than 24 hours
        // In production, parse JWT and check actual expiration
        if (blacklistedTokens.size() > 1000) {
            blacklistedTokens.clear();
            System.out.println("Cleaned up old blacklisted tokens");
        }
    }

    /**
     * Get count of blacklisted tokens (for monitoring)
     */
    public int getBlacklistedTokenCount() {
        return blacklistedTokens.size();
    }
}