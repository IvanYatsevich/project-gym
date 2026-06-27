package com.example.project_gym.model.request;

import java.time.LocalDateTime;

public record CreateTraineeRequest(String firstName,
                                   String lastName,
                                   LocalDateTime dateOfBirth,
                                   String address) {
}
