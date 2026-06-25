package com.example.project_gym.model.request;

import java.util.Date;

public record TraineeTrainingsFilterRequest(
    String traineeUsername,
    Date fromDate,
    Date toDate,
    String trainerName,
    String trainingType
) {
}
