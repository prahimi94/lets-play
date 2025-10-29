package com.example.lets_play.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Custom error controller to handle 404 and other HTTP errors properly
 */
@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<String> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            switch (statusCode) {
                case 404:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Route not found");
                case 405:
                    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                            .body("Method not allowed");
                case 400:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Bad request");
                case 401:
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("Unauthorized");
                case 403:
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Forbidden");
                case 500:
                default:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Internal server error");
            }
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unknown error occurred");
    }
}