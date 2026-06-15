package com.example.project_gym.service;

import com.example.project_gym.model.Training;
import com.example.project_gym.model.dto.dtoin.TrainingDtoIn;
import com.example.project_gym.repository.TraineeDaoImpl;
import com.example.project_gym.repository.TrainerDaoImpl;
import com.example.project_gym.repository.TrainingDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainingService {

    @Autowired
    private TrainingDaoImpl trainingDao;

    @Autowired
    private TrainerDaoImpl trainerDao;

    @Autowired
    private TraineeDaoImpl traineeDao;


    public Training create(TrainingDtoIn dto) {
        traineeDao.selectById(dto.traineeId())
                .orElseThrow(() -> new RuntimeException("Trainee not found: " + dto.traineeId()));

        trainerDao.selectById(dto.trainerId())
                .orElseThrow(() -> new RuntimeException("Trainer not found: " + dto.trainerId()));

        Training training = new Training();
        training.setTraineeId(dto.traineeId());
        training.setTrainerId(dto.trainerId());
        training.setTrainingName(dto.trainingName());
        training.setTrainingDate(dto.trainingDate());
        training.setTrainingDuration(dto.trainingDuration());
        training.setTrainingType(dto.trainingType());

        return trainingDao.create(training);
    }

    public Training select (Long id){
        return trainingDao.getById(id)
                .orElseThrow(() ->
                        new RuntimeException("Training with id " + id + " not found"));
    }
}
