package com.example.project_gym.model.request;

public record PasswordChangeRequest(
    String username,
    String oldPassword,
    String newPassword
) {
}
