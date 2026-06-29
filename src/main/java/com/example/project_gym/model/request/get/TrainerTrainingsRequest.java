package com.example.project_gym.model.request.get;

import java.time.LocalDateTime;

public record TrainerTrainingsRequest(String username,
                                      LocalDateTime fromDate,
                                      LocalDateTime toDate,
                                      String traineeName) {
}
