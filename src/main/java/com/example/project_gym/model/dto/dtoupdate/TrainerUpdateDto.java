package com.example.project_gym.model.dto.dtoupdate;

import com.example.project_gym.model.TrainingType;

public record TrainerUpdateDto(Boolean isActive,
                               TrainingType specialization) {
}
