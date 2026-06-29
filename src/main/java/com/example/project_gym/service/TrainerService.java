package com.example.project_gym.service;

import com.example.project_gym.domain.entity.*;
import com.example.project_gym.exception.BusinessRuleException;
import com.example.project_gym.exception.InactiveUserException;
import com.example.project_gym.exception.TrainerNotFoundException;
import com.example.project_gym.exception.TrainingTypeNotFoundException;
import com.example.project_gym.mapper.TrainerMapper;
import com.example.project_gym.mapper.TrainingMapper;
import com.example.project_gym.model.request.create.TrainerCreateRequest;
import com.example.project_gym.model.request.TrainerTrainingsRequest;
import com.example.project_gym.model.request.get.TrainerRequest;
import com.example.project_gym.model.request.update.TrainerUpdateRequest;
import com.example.project_gym.model.request.update.UserActivationRequest;
import com.example.project_gym.model.response.create.TrainerCreateResponse;
import com.example.project_gym.model.response.get.SimpleTrainingResponse;
import com.example.project_gym.model.response.get.TrainerResponse;
import com.example.project_gym.model.response.update.TrainerUpdateResponse;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.repository.idao.TrainingTypeDAO;
import com.example.project_gym.security.AuthenticationGuard;
import com.example.project_gym.utilservices.authenticatedservices.PasswordChangeService;
import com.example.project_gym.utilservices.guestservices.password.PasswordGenerator;
import com.example.project_gym.utilservices.guestservices.username.UniqueUserNameGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainerService {

    private TrainerDAO trainerDao;

    private TrainingTypeDAO trainingTypeDao;

    private PasswordChangeService passwordChangeService;

    private UniqueUserNameGenerator nameGenerator;

    private PasswordGenerator passwordGenerator;

    private AuthenticationGuard authGuard;

    private TrainerMapper trainerMapper;

    private TrainingMapper trainingMapper;

    public TrainerService(TrainerDAO trainerDao,
                          TrainingTypeDAO trainingTypeDao,
                          PasswordChangeService passwordChangeService,
                          UniqueUserNameGenerator nameGenerator,
                          PasswordGenerator passwordGenerator,
                          AuthenticationGuard authGuard,
                          TrainerMapper trainerMapper,
                          TrainingMapper trainingMapper) {
        this.trainerDao = trainerDao;
        this.trainingTypeDao = trainingTypeDao;
        this.passwordChangeService = passwordChangeService;
        this.nameGenerator = nameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.authGuard = authGuard;
        this.trainerMapper = trainerMapper;
        this.trainingMapper = trainingMapper;
    }



    @Transactional
    public TrainerCreateResponse create(TrainerCreateRequest trainerCreateRequest) {

        TrainingTypeEntity trainingTypeEntity = resolveTrainingType(trainerCreateRequest.specialization());


        User user = new User();
        TrainerEntity trainerEntity = trainerMapper.toEntity(trainerCreateRequest);
        trainerEntity.setTrainingTypeEntity(trainingTypeEntity);
        user.setFirstName(trainerCreateRequest.firstName());
        user.setLastName(trainerCreateRequest.lastName());
        user.setUserName(nameGenerator.generateUnique(trainerCreateRequest.firstName(), trainerCreateRequest.lastName()));
        user.setPassword(passwordGenerator.generatePassword());
        user.setActive(true);
        trainerEntity.setUser(user);

        return trainerMapper.toCreateResponse(trainerDao.create(trainerEntity));
    }

    @Transactional(readOnly = true)
    public TrainerResponse selectByUsername(TrainerRequest trainerRequest) {
        authGuard.requireAuthenticated();
        return trainerDao.getByUsername(trainerRequest.username())
                .map(trainerMapper::toGetResponse)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer with username " + trainerRequest.username() + " not found"));
    }


    @Transactional
    public TrainerUpdateResponse update(TrainerUpdateRequest updateTrainerRequest) {
        authGuard.requireAuthenticated();

        TrainerEntity existingTrainerEntity = trainerDao.getByUsername(updateTrainerRequest.username())
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found"));

        if (existingTrainerEntity.getUser().isActive() == updateTrainerRequest.isActive()) {
            throw new InactiveUserException("Trainer activation status is already " + updateTrainerRequest.isActive());
        }

        trainerMapper.updateEntity(updateTrainerRequest, existingTrainerEntity);
        existingTrainerEntity.getUser().setActive(updateTrainerRequest.isActive());
        existingTrainerEntity.setTrainingTypeEntity(updateTrainerRequest.specialization());


        return trainerMapper.toUpdateResponse(trainerDao.update(existingTrainerEntity));
    }
    @Transactional
    public void toggleActive(UserActivationRequest request) {
        authGuard.requireAuthenticated();
        TrainerEntity trainerEntity = trainerDao.getByUsername(request.username())
                .orElseThrow(() -> new TrainerNotFoundException("Trainer with username " + request.username() + " not found"));
        if (trainerEntity.getUser().isActive() == request.isActive()) {
            throw new InactiveUserException("Trainer activation status is already " + request.isActive());
        }
        trainerMapper.updateActiveStatus(request, trainerEntity);
        trainerDao.update(trainerEntity);
    }

    @Transactional(readOnly = true)
    public List<SimpleTrainingResponse> getTrainings(TrainerTrainingsRequest trainingsRequest) {
        authGuard.requireAuthenticated();
        List<TrainingEntity> trainings = trainerDao.getTrainings(
                trainingsRequest.trainerUsername(),
                trainingsRequest.fromDate(),
                trainingsRequest.toDate(),
                trainingsRequest.traineeName());
        return trainings.stream()
                .map(trainingMapper::toResponse)
                .toList();
    }

//    @Transactional(readOnly = true)
//    public TrainerEntity select(Long id) {
//        authGuard.requireAuthenticated();
//        return trainerDao.getById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));
//    }



    private TrainingTypeEntity resolveTrainingType(String trainingTypeName) {
        if (trainingTypeName == null || trainingTypeName.isBlank()) {
            throw new BusinessRuleException("Training type must be provided");
        }
        return trainingTypeDao.findByTrainingTypeName(trainingTypeName)
                .orElseThrow(() -> new TrainingTypeNotFoundException("Training type not found: " + trainingTypeName));
    }
}
