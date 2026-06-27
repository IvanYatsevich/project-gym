package com.example.project_gym.model.request;

import java.time.LocalDateTime;

public record UpdateTraineeRequest(Boolean isActive,
                                   String address,
                                   LocalDateTime dateOfBirth) {
    public UpdateTraineeRequest(Boolean isActive, String address) {
        this(isActive, address, null);
    }
}
