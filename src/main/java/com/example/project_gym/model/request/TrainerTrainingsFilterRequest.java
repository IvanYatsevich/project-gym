package com.example.project_gym.model.request;

import java.util.Date;

public record TrainerTrainingsFilterRequest(
    String trainerUsername,
    Date fromDate,
    Date toDate,
    String traineeName
) {
}
