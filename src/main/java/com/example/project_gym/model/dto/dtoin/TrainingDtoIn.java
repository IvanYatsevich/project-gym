package com.example.project_gym.model.dto.dtoin;

import com.example.project_gym.model.TrainingType;

import java.util.Date;

public record TrainingDtoIn(Long trainerId,
                            Long traineeId,
                            String trainingName,
                            TrainingType trainingType,
                            Date trainingDate,
                            Long trainingDuration) {
}
