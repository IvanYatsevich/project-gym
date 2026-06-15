package com.example.project_gym.model.dto.initdto;

public record TraineeInitDto(
        Long id,
        String firstName,
        String lastName,
        String userName,
        String password,
        boolean active,
        String dateOfBirth,
        String address,
        Long userId
) {
}
