package com.example.project_gym.model.response.get;

import com.example.project_gym.domain.entity.TrainingTypeEntity;

public record TraineeTrainerResponse(
        String trainerUsername,
        String firstName,
        String lastName,
        TrainingTypeEntity specialization) {
}
