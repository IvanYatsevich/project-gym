package com.example.project_gym.repository.idao;

import com.example.project_gym.domain.entity.TrainingTypeEntity;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeDAO {
	Optional<TrainingTypeEntity> findByTrainingTypeName(String trainingTypeName);
	List<TrainingTypeEntity> getAll();
}

