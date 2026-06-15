package com.example.project_gym.model.dto.dtoin;

import com.example.project_gym.model.TrainingType;

public record TrainerDtoIn(String firstName,
                           String lastName,
                           TrainingType specialization) {
}
