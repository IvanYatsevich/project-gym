package com.example.project_gym.model.response.update;

import com.example.project_gym.model.response.get.TraineeTrainerResponse;

import java.util.List;

public record TraineeTrainersUpdateResponse(List<TraineeTrainerResponse> trainers) {
}
