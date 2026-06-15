package com.example.project_gym.repository.idao;

import com.example.project_gym.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface ITrainerDAO {
    Trainer create(Trainer trainer);
    Optional<Trainer> selectById(Long id);
    Trainer update(Trainer trainer);
}
