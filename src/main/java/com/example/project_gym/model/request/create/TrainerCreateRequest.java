package com.example.project_gym.model.request.create;

import jakarta.validation.constraints.NotBlank;

public record TrainerCreateRequest(@NotBlank String firstName,
                                   @NotBlank String lastName,
                                   @NotBlank String specialization) {
}
