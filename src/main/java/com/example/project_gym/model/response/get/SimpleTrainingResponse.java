package com.example.project_gym.model.response.get;

import com.example.project_gym.domain.entity.TrainingTypeEntity;

import java.time.LocalDateTime;

public record SimpleTrainingResponse(String trainingName,
                                     LocalDateTime trainingDate,
                                     TrainingTypeEntity trainingTypeEntity,
                                     Long trainingDuration,
                                     String nameOfUser) {
}
