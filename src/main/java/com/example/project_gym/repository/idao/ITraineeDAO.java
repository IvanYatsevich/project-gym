package com.example.project_gym.repository.idao;

import com.example.project_gym.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface ITraineeDAO {
    Trainee create(Trainee trainee);
    boolean delete(Long id);
    Optional<Trainee> selectById(Long id);
    Trainee update(Trainee trainee);
}
