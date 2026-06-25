package com.example.project_gym.model.request;

import com.example.project_gym.domain.entity.TrainingType;

import java.util.Date;

public record CreateTrainingRequest(Long trainerId,
                                    Long traineeId,
                                    String trainingName,
                                    TrainingType trainingType,
                                    Date trainingDate,
                                    Long trainingDuration) {
}
