package com.example.project_gym.model.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record TraineeTrainingsRequest(
    @NotBlank String traineeUsername,
    LocalDateTime fromDate,
    LocalDateTime toDate,
    String trainerName,
    String trainingType
) {
}
