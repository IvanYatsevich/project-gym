package com.example.project_gym.service;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.model.request.CreateTrainingRequest;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.repository.idao.TrainingDAO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainingService {

    @Autowired
    private TrainingDAO trainingDao;

    @Autowired
    private TrainerDAO trainerDao;

    @Autowired
    private TraineeDAO traineeDao;


    @Transactional
    public TrainingEntity create(CreateTrainingRequest dto) {
        validateTrainingDtoInput(dto);

        TraineeEntity traineeEntity = traineeDao.findById(dto.traineeId())
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));

        TrainerEntity trainerEntity = trainerDao.findById(dto.trainerId())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));

        TrainingEntity trainingEntity = new TrainingEntity();
        trainingEntity.setTraineeEntity(traineeEntity);
        trainingEntity.setTrainerEntity(trainerEntity);
        trainingEntity.setTrainingName(dto.trainingName());
        trainingEntity.setTrainingDate(dto.trainingDate());
        trainingEntity.setTrainingDuration(dto.trainingDuration());
        trainingEntity.setTrainingType(dto.trainingType());

        return trainingDao.create(trainingEntity);
    }

    @Transactional(readOnly = true)
    public TrainingEntity select(Long id) {
        return trainingDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Training not found"));
    }

    @Transactional(readOnly = true)
    public List<TrainingEntity> getAll() {
        return trainingDao.getAll();
    }

    private void validateTrainingDtoInput(CreateTrainingRequest dto) {
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
