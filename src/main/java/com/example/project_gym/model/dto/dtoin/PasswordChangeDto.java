package com.example.project_gym.model.dto.dtoin;

public record PasswordChangeDto(
    String username,
    String oldPassword,
    String newPassword
) {
}
