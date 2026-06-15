package com.example.project_gym.model.dto.initdto;

public record TrainingInitDto(
        Long trainerId,
        Long traineeId,
        String trainingName,
        String trainingType,
        String trainingDate,
        Long trainingDuration
) {
}
