package com.example.project_gym.model.request.get;

import com.example.project_gym.domain.entity.TrainingTypeEntity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TraineeTrainingsRequest(@NotNull String username,
                                      LocalDateTime fromDate,
                                      LocalDateTime toDate,
                                      String trainerName,
                                      TrainingTypeEntity trainingTypeEntity) {
}
