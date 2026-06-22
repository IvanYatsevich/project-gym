package com.example.project_gym.model.dto.dtoin;

import java.util.Date;

public record TrainerTrainingsFilterDto(
    String trainerUsername,
    Date fromDate,
    Date toDate,
    String traineeName
) {
}
