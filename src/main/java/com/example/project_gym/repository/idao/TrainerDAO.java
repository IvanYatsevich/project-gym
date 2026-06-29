package com.example.project_gym.repository.idao;

import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrainerDAO {
    TrainerEntity create(TrainerEntity trainerEntity);
    TrainerEntity update(TrainerEntity trainerEntity);
    Optional<TrainerEntity> getByUsername(String username);
    boolean deleteByUsername(String username);
    List<TrainingEntity> getTrainings(String trainerUsername, LocalDateTime fromDate, LocalDateTime toDate, String traineeName);
}
