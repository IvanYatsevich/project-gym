package com.example.project_gym.repository.idao;

import com.example.project_gym.domain.entity.TrainingType;

import java.util.Optional;

public interface TrainingTypeDAO {
	Optional<TrainingType> findByTrainingTypeName(String trainingTypeName);
}

