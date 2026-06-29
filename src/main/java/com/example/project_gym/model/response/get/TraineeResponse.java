package com.example.project_gym.model.response.get;

import java.time.LocalDateTime;
import java.util.List;

public record TraineeResponse(
        String firstName,
        String lastName,
        LocalDateTime dateOfBirth,
        String address,
        Boolean isActive,
        List<TraineeTrainerResponse> assignedTrainers){


}
