package com.example.project_gym.repository.idao;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TraineeDAO {
    TraineeEntity create(TraineeEntity traineeEntity);
    boolean delete(Long id);
    Optional<TraineeEntity> findById(Long id);
    TraineeEntity update(TraineeEntity traineeEntity);
    Optional<TraineeEntity> findByUsername(String username);
    boolean deleteByUsername(String username);
    List<TrainingEntity> getTrainings(String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType);
    List<TrainerEntity> findUnassignedTrainers(String traineeUsername);
    void updateTrainersList(TraineeEntity traineeEntity, List<TrainerEntity> trainerEntities);
}
