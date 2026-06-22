package com.example.project_gym.service;

import com.example.project_gym.model.UserType;
import com.example.project_gym.model.dto.dtoin.LoginResultDto;
import com.example.project_gym.repository.idao.ITraineeDAO;
import com.example.project_gym.repository.idao.ITrainerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;

@Service
@Transactional(readOnly = true)
public class AuthorizationService {

    @Autowired
    private ITraineeDAO traineeDAO;

    @Autowired
    private ITrainerDAO trainerDAO;

    public boolean authenticateTrainee(String username, String password) {
        return traineeDAO.selectByUsername(username)
                .map(trainee -> trainee.getUser().getPassword().equals(password))
                .orElse(false);
    }

    public boolean authenticateTrainer(String username, String password) {
        return trainerDAO.selectByUsername(username)
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

    public UserType authenticate(LoginResultDto loginResultDto) throws AuthenticationException {
        return authenticate(loginResultDto.username(), loginResultDto.password());
    }
}
