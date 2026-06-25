package com.example.project_gym.model.request;

import java.util.Date;

public record UpdateTraineeRequest(Boolean isActive,
                                   String address,
                                   Date dateOfBirth) {
    public UpdateTraineeRequest(Boolean isActive, String address) {
        this(isActive, address, null);
    }
}
