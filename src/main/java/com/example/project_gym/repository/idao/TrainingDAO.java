package com.example.project_gym.repository.idao;

import com.example.project_gym.domain.entity.TrainingEntity;

import java.util.List;
import java.util.Optional;

public interface TrainingDAO {
    TrainingEntity create(TrainingEntity trainingEntity);
    Optional<TrainingEntity> findById(Long id);
    List<TrainingEntity> getAll();
}
