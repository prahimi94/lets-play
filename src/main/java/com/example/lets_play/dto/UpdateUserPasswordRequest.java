package com.example.lets_play.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateUserPasswordRequest {
    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;

    // Getters and Setters
    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
