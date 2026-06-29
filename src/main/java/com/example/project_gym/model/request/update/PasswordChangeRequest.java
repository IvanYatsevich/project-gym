package com.example.project_gym.model.request.update;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(
    @NotBlank String username,
    @NotBlank String oldPassword,
    @NotBlank String newPassword
) {
}
