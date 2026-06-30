package com.example.project_gym.utilservices.authenticatedservices;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.model.request.PasswordChangeRequest;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.security.AuthenticationGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordChangeService {

    @Autowired
    private TraineeDAO traineeDao;

    @Autowired
    private TrainerDAO trainerDao;

    private AuthenticationGuard authGuard;

    @Autowired
    public void setAuthenticationGuard(AuthenticationGuard authGuard) {
        this.authGuard = authGuard;
    }

    @Transactional
    public void changeTraineePassword(PasswordChangeRequest passwordChangeRequest) {
        authGuard.requireAuthenticated();
        TraineeEntity traineeEntity = traineeDao.findByUsername(passwordChangeRequest.username())
                .orElseThrow(() -> new EntityNotFoundException("Trainee with username " + passwordChangeRequest.username() + " not found"));

        if (!traineeEntity.getUser().getPassword().equals(passwordChangeRequest.oldPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        traineeEntity.getUser().setPassword(passwordChangeRequest.newPassword());
        traineeDao.update(traineeEntity);
    }
    @Transactional
    public void changeTrainerPassword(PasswordChangeRequest passwordChangeRequest) {
        authGuard.requireAuthenticated();
        TrainerEntity trainerEntity = trainerDao.findByUsername(passwordChangeRequest.username())
                .orElseThrow(() -> new EntityNotFoundException("Trainer with username " + passwordChangeRequest.username() + " not found"));

        if (!trainerEntity.getUser().getPassword().equals(passwordChangeRequest.oldPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        trainerEntity.getUser().setPassword(passwordChangeRequest.newPassword());
        trainerDao.update(trainerEntity);
    }
}

