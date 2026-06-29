package com.example.project_gym.model.request.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TraineeUpdateRequest(@NotBlank String username,
                                   @NotBlank String firstName,
                                   @NotBlank String lastName,
                                   LocalDateTime dateOfBirth,
                                   String address,
                                   @NotNull Boolean isActive  ) {



}
