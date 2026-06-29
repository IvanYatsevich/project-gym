package com.example.project_gym.model.request.create;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record TraineeCreateRequest(@NotBlank String firstName,
                                   @NotBlank String lastName,
                                   LocalDateTime dateOfBirth,
                                   String address) {
}
