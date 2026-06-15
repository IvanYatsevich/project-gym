package com.example.project_gym.model.dto.initdto;

public record TrainerInitDto(
        Long id,
        String firstName,
        String lastName,
        String userName,
        String password,
        boolean active,
        String trainingType,
        Long userId
) {
}
