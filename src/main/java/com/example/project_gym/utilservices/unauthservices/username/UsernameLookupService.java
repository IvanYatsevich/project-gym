package com.example.project_gym.utilservices.unauthservices.username;

import com.example.project_gym.repository.idao.ITraineeDAO;
import com.example.project_gym.repository.idao.ITrainerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsernameLookupService {

    private ITrainerDAO trainerDao;
    private ITraineeDAO traineeDao;

    @Autowired
    public void setTrainerDao(ITrainerDAO trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(ITraineeDAO traineeDao) {
        this.traineeDao = traineeDao;
    }

    public boolean existsByUserName(String userName) {
        return trainerDao.selectByUsername(userName).isPresent() || traineeDao.selectByUsername(userName).isPresent();
    }
}
