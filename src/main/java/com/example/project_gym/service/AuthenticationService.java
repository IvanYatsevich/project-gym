package com.example.project_gym.service;

import com.example.project_gym.domain.entity.UserType;
import com.example.project_gym.exception.AuthenticationFailedException;
import com.example.project_gym.model.request.LoginRequest;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.security.AuthorizationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthenticationService {

    @Autowired
    private TraineeDAO traineeDAO;

    @Autowired
    private TrainerDAO trainerDAO;


    private AuthorizationContext authorizationContext;

    @Autowired
    public void setAuthorizationContext(AuthorizationContext authorizationContext) {
        this.authorizationContext = authorizationContext;
    }

    private boolean authenticateTrainee(String username, String password) {
        return traineeDAO.getByUsername(username)
                .map(trainee -> trainee.getUser().getPassword().equals(password))
                .orElse(false);
    }

    private boolean authenticateTrainer(String username, String password) {
        return trainerDAO.getByUsername(username)
                .map(trainer -> trainer.getUser().getPassword().equals(password))
                .orElse(false);
    }

    public UserType authenticate(String username, String password) {
        if (authenticateTrainee(username, password)) {
            authorizationContext.authenticate(username, UserType.TRAINEE);
            return UserType.TRAINEE;
        }

        if (authenticateTrainer(username, password)) {
            authorizationContext.authenticate(username, UserType.TRAINER);
            return UserType.TRAINER;
        }

        authorizationContext.clear();
        throw new AuthenticationFailedException("Username or password are invalid");
    }

    public UserType authenticate(LoginRequest loginRequest) {
        return authenticate(loginRequest.username(), loginRequest.password());
    }

    public void logout() {
        authorizationContext.clear();
    }
}
