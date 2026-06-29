package com.example.project_gym.model.response.get;

import java.util.List;

public record MultipleTrainingsResponse(List<SimpleTrainingResponse> trainings) {
}
