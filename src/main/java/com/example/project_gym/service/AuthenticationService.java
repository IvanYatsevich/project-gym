package com.example.project_gym.service;

import com.example.project_gym.domain.entity.UserType;
import com.example.project_gym.model.request.LoginRequest;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;

@Service
@Transactional(readOnly = true)
public class AuthenticationService {

    @Autowired
    private TraineeDAO traineeDAO;

    @Autowired
    private TrainerDAO trainerDAO;

    public boolean authenticateTrainee(String username, String password) {
        return traineeDAO.findByUsername(username)
                .map(trainee -> trainee.getUser().getPassword().equals(password))
                .orElse(false);
    }

    public boolean authenticateTrainer(String username, String password) {
        return trainerDAO.findByUsername(username)
                .map(trainer -> trainer.getUser().getPassword().equals(password))
                .orElse(false);
    }

    public UserType authenticate(String username, String password) throws AuthenticationException {
        if (authenticateTrainee(username, password)) {
            return UserType.TRAINEE;
        }

        if (authenticateTrainer(username, password)) {
            return UserType.TRAINER;
        }

        throw new AuthenticationException("Username or password are invalid");
    }

    public UserType authenticate(LoginRequest loginRequest) throws AuthenticationException {
        return authenticate(loginRequest.username(), loginRequest.password());
    }
}
