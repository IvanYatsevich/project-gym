package com.example.project_gym.model.request.update;

import com.example.project_gym.domain.entity.TrainingTypeEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerUpdateRequest(@NotBlank String username,
                                   @NotBlank String firstName,
                                   @NotBlank String lastName,
                                   TrainingTypeEntity specialization,
                                   @NotNull Boolean isActive) {
}
