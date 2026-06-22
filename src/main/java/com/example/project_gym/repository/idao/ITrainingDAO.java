package com.example.project_gym.repository.idao;

import com.example.project_gym.model.Training;

import java.util.List;
import java.util.Optional;

public interface ITrainingDAO {
    Training create(Training training);
    Optional<Training> getById(Long id);
    List<Training> getAll();
}
