package com.example.project_gym.service;

import com.example.project_gym.model.Training;
import com.example.project_gym.model.dto.dtoin.TrainingDtoIn;
import com.example.project_gym.repository.idao.ITraineeDAO;
import com.example.project_gym.repository.idao.ITrainerDAO;
import com.example.project_gym.repository.idao.ITrainingDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainingService {

    @Autowired
    private ITrainingDAO trainingDao;

    @Autowired
    private ITrainerDAO trainerDao;

    @Autowired
    private ITraineeDAO traineeDao;


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
