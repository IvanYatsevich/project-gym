package com.example.project_gym.model.request.update;

import com.example.project_gym.model.request.get.TrainerRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TraineeTrainersUpdateRequest(@NotBlank String username,
                                           @NotNull @NotEmpty List<@Valid TrainerRequest> trainers) {
}
