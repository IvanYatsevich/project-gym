package com.example.project_gym.utilservices.authservices;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.dto.dtoin.PasswordChangeDto;
import com.example.project_gym.repository.idao.ITraineeDAO;
import com.example.project_gym.repository.idao.ITrainerDAO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PasswordChangeService {

    @Autowired
    private ITraineeDAO traineeDao;

    @Autowired
    private ITrainerDAO trainerDao;

    public void changeTraineePassword(PasswordChangeDto passwordChangeDto) {
        Trainee trainee = traineeDao.selectByUsername(passwordChangeDto.username())
                .orElseThrow(() -> new EntityNotFoundException("Trainee with username " + passwordChangeDto.username() + " not found"));

        if (!trainee.getUser().getPassword().equals(passwordChangeDto.oldPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        trainee.getUser().setPassword(passwordChangeDto.newPassword());
        traineeDao.update(trainee);
    }

    public void changeTrainerPassword(PasswordChangeDto passwordChangeDto) {
        Trainer trainer = trainerDao.selectByUsername(passwordChangeDto.username())
                .orElseThrow(() -> new EntityNotFoundException("Trainer with username " + passwordChangeDto.username() + " not found"));

        if (!trainer.getUser().getPassword().equals(passwordChangeDto.oldPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        trainer.getUser().setPassword(passwordChangeDto.newPassword());
        trainerDao.update(trainer);
    }
}

