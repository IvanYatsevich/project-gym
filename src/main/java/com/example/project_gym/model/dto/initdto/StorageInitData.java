package com.example.project_gym.model.dto.initdto;

import java.util.List;

public record StorageInitData(
        List<TrainerInitDto> trainers,
        List<TraineeInitDto> trainees,
        List<TrainingInitDto> trainings
) {
}
