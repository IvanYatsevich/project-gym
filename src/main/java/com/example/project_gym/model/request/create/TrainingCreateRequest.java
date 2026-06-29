package com.example.project_gym.model.request.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record TrainingCreateRequest(@NotBlank String trainerUsername,
                                    @NotBlank String traineeUsername,
                                    @NotBlank String trainingName,
                                    @NotNull LocalDateTime trainingDate,
                                    @NotNull @Positive Long trainingDuration) {
}
