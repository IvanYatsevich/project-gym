package com.example.project_gym.service;

import com.example.project_gym.domain.entity.UserType;
import com.example.project_gym.model.request.LoginRequest;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.security.AuthorizationContext;
import com.example.project_gym.utilservices.guestservices.username.UniqueUserNameGenerator;
import lombok.Setter;
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


    private AuthorizationContext authorizationContext;

    @Autowired
    public void setAuthorizationContext(AuthorizationContext authorizationContext) {
        this.authorizationContext = authorizationContext;
    }

    private boolean authenticateTrainee(String username, String password) {
        return traineeDAO.findByUsername(username)
                .map(trainee -> trainee.getUser().getPassword().equals(password))
                .orElse(false);
    }

    private boolean authenticateTrainer(String username, String password) {
        return trainerDAO.findByUsername(username)
                .map(trainer -> trainer.getUser().getPassword().equals(password))
                .orElse(false);
    }

    public UserType authenticate(String username, String password) throws AuthenticationException {
        if (authenticateTrainee(username, password)) {
            authorizationContext.authenticate(username, UserType.TRAINEE);
            return UserType.TRAINEE;
        }

        if (authenticateTrainer(username, password)) {
            authorizationContext.authenticate(username, UserType.TRAINER);
            return UserType.TRAINER;
        }

        authorizationContext.clear();
        throw new AuthenticationException("Username or password are invalid");
    }

    public UserType authenticate(LoginRequest loginRequest) throws AuthenticationException {
        return authenticate(loginRequest.username(), loginRequest.password());
    }

    public void logout() {
        authorizationContext.clear();
    }
}
