package com.example.project_gym.utilservices.guestservices.username;

import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsernameLookupService {

    private TrainerDAO trainerDao;
    private TraineeDAO traineeDao;

    @Autowired
    public void setTrainerDao(TrainerDAO trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDAO traineeDao) {
        this.traineeDao = traineeDao;
    }

    public boolean existsByUserName(String userName) {
        return trainerDao.findByUsername(userName).isPresent() || traineeDao.findByUsername(userName).isPresent();
    }
}
