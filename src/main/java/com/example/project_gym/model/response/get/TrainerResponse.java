package com.example.project_gym.model.response.get;

import com.example.project_gym.domain.entity.TrainingTypeEntity;

import java.util.List;

public record TrainerResponse(String firstName,
                              String lastName,
                              TrainingTypeEntity specialization,
                              Boolean isActive,
                              List<TrainerTraineesResponse> trainees) {
}
