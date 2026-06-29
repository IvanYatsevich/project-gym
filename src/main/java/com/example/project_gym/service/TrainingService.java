package com.example.project_gym.service;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.exception.TraineeNotFoundException;
import com.example.project_gym.exception.TrainerNotFoundException;
import com.example.project_gym.exception.TrainingConflictException;
import com.example.project_gym.exception.TrainingNotFoundException;
import com.example.project_gym.exception.InvalidTrainingDataException;
import com.example.project_gym.mapper.TrainingMapper;
import com.example.project_gym.model.request.create.TrainingCreateRequest;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.repository.idao.TrainingDAO;
import com.example.project_gym.security.AuthenticationGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingService {

    private TrainingDAO trainingDao;

    private TrainerDAO trainerDao;

    private TraineeDAO traineeDao;

    private TrainingMapper trainingMapper;

    private AuthenticationGuard authGuard;


    public TrainingService(TrainingDAO trainingDao, TrainerDAO trainerDao, TraineeDAO traineeDao, TrainingMapper trainingMapper, AuthenticationGuard authGuard) {
        this.trainingDao = trainingDao;
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
        this.trainingMapper = trainingMapper;
        this.authGuard = authGuard;
       ;
    }

    @Transactional
    public void create(TrainingCreateRequest dto) {
        authGuard.requireAuthenticated();

        if (dto.trainingDuration() == null || dto.trainingDuration() <= 0) {
            throw new InvalidTrainingDataException("Training duration must be positive");
        }


        TraineeEntity traineeEntity = traineeDao.getByUsername(dto.traineeUsername())
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found"));

        TrainerEntity trainerEntity = trainerDao.getByUsername(dto.trainerUsername())
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found"));

        if (!traineeEntity.getUser().isActive() || !trainerEntity.getUser().isActive()) {
            throw new TrainingConflictException("Cannot create training for inactive user");
        }

        TrainingEntity trainingEntity = trainingMapper.toEntity(dto);
        trainingEntity.setTraineeEntity(traineeEntity);
        trainingEntity.setTrainerEntity(trainerEntity);
        trainingEntity.setTrainingTypeEntity(trainerEntity.getTrainingTypeEntity());
        trainingDao.create(trainingEntity);
    }
    @Transactional(readOnly = true)
    public TrainingEntity select(Long id) {
        authGuard.requireAuthenticated();
        return trainingDao.findById(id)
                .orElseThrow(() -> new TrainingNotFoundException("Training not found"));
    }

    @Transactional(readOnly = true)
    public List<TrainingEntity> getAll() {
        authGuard.requireAuthenticated();
        return trainingDao.getAll();
    }




}
