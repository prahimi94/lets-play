package com.example.lets_play.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter to enforce HTTPS for all requests
 */
@Component
@Order(1) // Run before other filters
public class HttpsEnforcementFilter implements Filter {

    @Value("${app.enforce-https:true}")
    private boolean enforceHttps;

    @Value("${server.port:8080}")
    private String serverPort;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Skip HTTPS enforcement in development/testing
        if (!enforceHttps) {
            chain.doFilter(request, response);
            return;
        }

        // Check if request is already HTTPS
        boolean isSecure = httpRequest.isSecure();
        
        // Check X-Forwarded-Proto header (for load balancers/proxies)
        String forwardedProto = httpRequest.getHeader("X-Forwarded-Proto");
        if ("https".equals(forwardedProto)) {
            isSecure = true;
        }

        // If not secure, redirect to HTTPS
        if (!isSecure) {
            String httpsUrl = buildHttpsUrl(httpRequest);
            
            // Log the redirect attempt
            System.out.println("🔒 Redirecting HTTP to HTTPS: " + httpRequest.getRequestURL());
            
            httpResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            httpResponse.setHeader("Location", httpsUrl);
            httpResponse.getWriter().write("Redirecting to HTTPS: " + httpsUrl);
            return;
        }

        // Add security headers for HTTPS responses
        addSecurityHeaders(httpResponse);
        
        chain.doFilter(request, response);
    }

    private String buildHttpsUrl(HttpServletRequest request) {
        StringBuilder httpsUrl = new StringBuilder();
        httpsUrl.append("https://");
        httpsUrl.append(request.getServerName());
        
        // Add port if not standard HTTPS port (443)
        int port = request.getServerPort();
        if (port != 443 && port != 80) {
            // For development, typically use 8443 for HTTPS
            httpsUrl.append(":").append(port == 8080 ? 8443 : port);
        }
        
        httpsUrl.append(request.getRequestURI());
        
        if (request.getQueryString() != null) {
            httpsUrl.append("?").append(request.getQueryString());
        }
        
        return httpsUrl.toString();
    }

    private void addSecurityHeaders(HttpServletResponse response) {
        // Strict Transport Security - force HTTPS for 1 year
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        
        // Prevent mixed content
        response.setHeader("Content-Security-Policy", "upgrade-insecure-requests");
        
        // Prevent clickjacking
        response.setHeader("X-Frame-Options", "DENY");
        
        // Prevent MIME type sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");
        
        // XSS Protection
        response.setHeader("X-XSS-Protection", "1; mode=block");
    }
}