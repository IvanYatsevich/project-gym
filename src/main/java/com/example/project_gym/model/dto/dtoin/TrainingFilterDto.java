package com.example.project_gym.model.dto.dtoin;

import java.util.Date;

public record TrainingFilterDto(
    String traineeUsername,
    Date fromDate,
    Date toDate,
    String trainerName,
    String trainingType
) {
}
