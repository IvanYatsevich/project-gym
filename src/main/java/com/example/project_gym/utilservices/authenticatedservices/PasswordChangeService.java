package com.example.project_gym.utilservices.authenticatedservices;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.exception.InvalidPasswordChangeException;
import com.example.project_gym.exception.PasswordMismatchException;
import com.example.project_gym.exception.UserNotFoundException;
import com.example.project_gym.model.request.update.PasswordChangeRequest;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.security.AuthenticationGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PasswordChangeService {

    private TraineeDAO traineeDao;

    private TrainerDAO trainerDao;

    private AuthenticationGuard authGuard;

    public PasswordChangeService(TraineeDAO traineeDao, TrainerDAO trainerDao, AuthenticationGuard authGuard) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.authGuard = authGuard;
    }

    public void changeTraineePassword(PasswordChangeRequest passwordChangeRequest) {
        authGuard.requireAuthenticated();
        TraineeEntity traineeEntity = traineeDao.getByUsername(passwordChangeRequest.username())
                .orElseThrow(() -> new UserNotFoundException("Trainee with username " + passwordChangeRequest.username() + " not found"));

        if (passwordChangeRequest.oldPassword().equals(passwordChangeRequest.newPassword())) {
            throw new InvalidPasswordChangeException("New password must differ from old password");
        }

        if (!traineeEntity.getUser().getPassword().equals(passwordChangeRequest.oldPassword())) {
            throw new PasswordMismatchException("Old password is incorrect");
        }

        traineeEntity.getUser().setPassword(passwordChangeRequest.newPassword());
        traineeDao.update(traineeEntity);
    }

    public void changeTrainerPassword(PasswordChangeRequest passwordChangeRequest) {
        authGuard.requireAuthenticated();
        TrainerEntity trainerEntity = trainerDao.getByUsername(passwordChangeRequest.username())
                .orElseThrow(() -> new UserNotFoundException("Trainer with username " + passwordChangeRequest.username() + " not found"));

        if (passwordChangeRequest.oldPassword().equals(passwordChangeRequest.newPassword())) {
            throw new InvalidPasswordChangeException("New password must differ from old password");
        }

        if (!trainerEntity.getUser().getPassword().equals(passwordChangeRequest.oldPassword())) {
            throw new PasswordMismatchException("Old password is incorrect");
        }

        trainerEntity.getUser().setPassword(passwordChangeRequest.newPassword());
        trainerDao.update(trainerEntity);
    }
}

