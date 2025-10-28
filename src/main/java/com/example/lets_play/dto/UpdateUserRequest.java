package com.example.lets_play.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateUserRequest {
    @NotBlank
    private String name;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
