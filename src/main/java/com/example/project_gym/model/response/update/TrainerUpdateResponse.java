package com.example.project_gym.model.response.update;

import com.example.project_gym.domain.entity.TrainingTypeEntity;
import com.example.project_gym.model.response.get.TrainerTraineesResponse;

import java.util.List;

public record TrainerUpdateResponse(String username,
                                    String firstName,
                                    String lastName,
                                    TrainingTypeEntity specialization,
                                    Boolean isActive,
                                    List<TrainerTraineesResponse> trainees) {
}
