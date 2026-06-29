package com.example.project_gym.model.request.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserActivationRequest(@NotBlank String username,
                                    @NotNull Boolean isActive) {
}
