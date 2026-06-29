package com.example.project_gym.model.response.update;

import com.example.project_gym.model.response.get.TraineeTrainerResponse;

import java.time.LocalDateTime;
import java.util.List;

public record TraineeUpdateResponse(String username,
                                    String firstName,
                                    String lastName,
                                    LocalDateTime dateOfBirth,
                                    String address,
                                    Boolean isActive,
                                    List<TraineeTrainerResponse> trainers) {
}
