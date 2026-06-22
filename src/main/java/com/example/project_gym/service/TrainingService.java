package com.example.project_gym.service;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import com.example.project_gym.model.dto.dtoin.TrainingDtoIn;
import com.example.project_gym.repository.idao.ITraineeDAO;
import com.example.project_gym.repository.idao.ITrainerDAO;
import com.example.project_gym.repository.idao.ITrainingDAO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainingService {

    @Autowired
    private ITrainingDAO trainingDao;

    @Autowired
    private ITrainerDAO trainerDao;

    @Autowired
    private ITraineeDAO traineeDao;


    @Transactional
    public Training create(TrainingDtoIn dto) {
        validateTrainingDtoInput(dto);

        Trainee trainee = traineeDao.selectById(dto.traineeId())
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));

        Trainer trainer = trainerDao.selectById(dto.trainerId())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(dto.trainingName());
        training.setTrainingDate(dto.trainingDate());
        training.setTrainingDuration(dto.trainingDuration());
        training.setTrainingType(dto.trainingType());

        return trainingDao.create(training);
    }

    @Transactional(readOnly = true)
    public Training select(Long id) {
        return trainingDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Training not found"));
    }

    @Transactional(readOnly = true)
    public List<Training> getAll() {
        return trainingDao.getAll();
    }

    private void validateTrainingDtoInput(TrainingDtoIn dto) {
        if (dto.traineeId() == null) {
            throw new IllegalArgumentException("Trainee ID is required");
        }
        if (dto.trainerId() == null) {
            throw new IllegalArgumentException("Trainer ID is required");
        }
        if (dto.trainingName() == null || dto.trainingName().trim().isEmpty()) {
            throw new IllegalArgumentException("Training name is required");
        }
        if (dto.trainingDate() == null) {
            throw new IllegalArgumentException("Training date is required");
        }
        if (dto.trainingDuration() == null || dto.trainingDuration() <= 0) {
            throw new IllegalArgumentException("Training duration must be greater than 0");
        }
        if (dto.trainingType() == null) {
            throw new IllegalArgumentException("Training type is required");
        }
    }


}
