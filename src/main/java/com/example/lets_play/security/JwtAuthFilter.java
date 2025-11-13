package com.example.lets_play.security;

import com.example.lets_play.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthFilter(JwtService jwtService, TokenBlacklistService tokenBlacklistService) {
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String email = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            
            // Check if token is blacklisted
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                System.out.println("🚫 Blacklisted token rejected");
                filterChain.doFilter(request, response);
                return;
            }
            
            try {
                email = jwtService.extractEmail(token);
            } catch (Exception e) {
                // invalid token
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String role = jwtService.extractRole(authHeader);
                String authority = "ROLE_" + role;
                
                // Debug logging
                System.out.println("🔍 JWT Debug - Email: " + email + ", Role: " + role + ", Authority: " + authority);
                
                SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority);
                
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(grantedAuthority));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                System.out.println("✅ Authentication set successfully for user: " + email + " with role: " + role);
            } catch (Exception e) {
                System.err.println("❌ Error setting authentication: " + e.getMessage());
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }
}
