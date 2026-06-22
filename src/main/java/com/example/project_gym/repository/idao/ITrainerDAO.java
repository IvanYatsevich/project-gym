package com.example.project_gym.repository.idao;

import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ITrainerDAO {
    Trainer create(Trainer trainer);
    Optional<Trainer> selectById(Long id);
    Trainer update(Trainer trainer);
    Optional<Trainer> selectByUsername(String username);
    boolean deleteByUsername(String username);
    List<Training> getTrainings(String trainerUsername, Date fromDate, Date toDate, String traineeName);
}
