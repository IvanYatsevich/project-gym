package com.example.project_gym.model.request;

import java.time.LocalDateTime;

public record TraineeTrainingsFilterRequest(
    String traineeUsername,
    LocalDateTime fromDate,
    LocalDateTime toDate,
    String trainerName,
    String trainingType
) {
}
