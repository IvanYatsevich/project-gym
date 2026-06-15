package com.example.project_gym.utilservices;

import com.example.project_gym.repository.TraineeDaoImpl;
import com.example.project_gym.repository.TrainerDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsernameLookupService {

    private TrainerDaoImpl trainerDao;
    private TraineeDaoImpl traineeDao;

    @Autowired
    public void setTrainerDao(TrainerDaoImpl trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDaoImpl traineeDao) {
        this.traineeDao = traineeDao;
    }

    public boolean existsByUserName(String userName) {
        return trainerDao.existsByUserName(userName) || traineeDao.existsByUserName(userName);
    }
}
