package com.example.project_gym.model.request;

import java.time.LocalDateTime;

public record TrainerTrainingsFilterRequest(
    String trainerUsername,
    LocalDateTime fromDate,
    LocalDateTime toDate,
    String traineeName
) {
}
