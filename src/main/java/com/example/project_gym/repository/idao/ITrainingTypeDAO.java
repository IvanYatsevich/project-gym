package com.example.project_gym.repository.idao;

import com.example.project_gym.model.TrainingType;

import java.util.Optional;

public interface ITrainingTypeDAO {
	Optional<TrainingType> findByTrainingTypeName(String trainingTypeName);
}

