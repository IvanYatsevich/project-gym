package com.example.project_gym.model.dto.dtoupdate;

import java.util.Date;

public record TraineeUpdateDto(Boolean isActive,
                               String address,
                               Date dateOfBirth) {
    public TraineeUpdateDto(Boolean isActive, String address) {
        this(isActive, address, null);
    }
}
