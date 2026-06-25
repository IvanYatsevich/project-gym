package com.example.project_gym.repository.idao;

import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TrainerDAO {
    TrainerEntity create(TrainerEntity trainerEntity);
    Optional<TrainerEntity> findById(Long id);
    TrainerEntity update(TrainerEntity trainerEntity);
    Optional<TrainerEntity> findByUsername(String username);
    boolean deleteByUsername(String username);
    List<TrainingEntity> getTrainings(String trainerUsername, Date fromDate, Date toDate, String traineeName);
}
