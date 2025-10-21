package com.example.lets_play.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY = "ZmFrZXNlY3JldGtleWZha2VzZWNyZXRrZXlmYWtlc2VjcmV0a2V5";

    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24; // 1 day

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // generating token with email and role
    public String generateToken(String subject, String role) {
        return Jwts.builder()
                .setSubject(subject)          // usually email
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String authHeader) {
        if (authHeader.startsWith("Bearer ")) {
            authHeader = authHeader.substring(7);
        }
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authHeader)
                .getBody();
        return claims.getSubject();
    }

    public String extractRole(String authHeader) {
        if (authHeader.startsWith("Bearer ")) {
            authHeader = authHeader.substring(7);
        }
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authHeader)
                .getBody();
        return claims.get("role", String.class);
    }
}
