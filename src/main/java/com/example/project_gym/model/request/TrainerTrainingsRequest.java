package com.example.project_gym.model.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record TrainerTrainingsRequest(
    @NotBlank String trainerUsername,
    LocalDateTime fromDate,
    LocalDateTime toDate,
    String traineeName
) {
}
