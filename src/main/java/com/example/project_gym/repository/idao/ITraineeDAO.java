package com.example.project_gym.repository.idao;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ITraineeDAO {
    Trainee create(Trainee trainee);
    boolean delete(Long id);
    Optional<Trainee> selectById(Long id);
    Trainee update(Trainee trainee);
    Optional<Trainee> selectByUsername(String username);
    boolean deleteByUsername(String username);
    List<Training> getTrainings(String traineeUsername, Date fromDate, Date toDate, String trainerName, String trainingType);
    List<Trainer> findUnassignedTrainers(String traineeUsername);
    void updateTrainersList(Trainee trainee, List<Trainer> trainers);
}
