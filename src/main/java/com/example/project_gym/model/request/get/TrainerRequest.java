package com.example.project_gym.model.request.get;

import jakarta.validation.constraints.NotBlank;

public record TrainerRequest(@NotBlank String username) {
}
