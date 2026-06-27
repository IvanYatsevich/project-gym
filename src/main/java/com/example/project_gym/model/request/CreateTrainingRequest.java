package com.example.project_gym.model.request;

import com.example.project_gym.domain.entity.TrainingType;

import java.time.LocalDateTime;

public record CreateTrainingRequest(Long trainerId,
                                    Long traineeId,
                                    String trainingName,
                                    TrainingType trainingType,
                                    LocalDateTime trainingDate,
                                    Long trainingDuration) {
}
