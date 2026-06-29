package com.example.project_gym.service;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.domain.entity.User;
import com.example.project_gym.exception.BusinessRuleException;
import com.example.project_gym.exception.DuplicateUsernameException;
import com.example.project_gym.exception.InactiveUserException;
import com.example.project_gym.exception.InvalidTrainerAssignmentException;
import com.example.project_gym.exception.TraineeNotFoundException;
import com.example.project_gym.mapper.TraineeMapper;
import com.example.project_gym.mapper.TrainerMapper;
import com.example.project_gym.mapper.TrainingMapper;
import com.example.project_gym.model.request.update.PasswordChangeRequest;
import com.example.project_gym.model.request.create.TraineeCreateRequest;
import com.example.project_gym.model.request.TraineeTrainingsRequest;
import com.example.project_gym.model.request.get.TraineeRequest;
import com.example.project_gym.model.request.update.TraineeTrainersUpdateRequest;
import com.example.project_gym.model.request.update.TraineeUpdateRequest;
import com.example.project_gym.model.request.update.UserActivationRequest;
import com.example.project_gym.model.response.create.TraineeCreateResponse;
import com.example.project_gym.model.response.get.SimpleTrainingResponse;
import com.example.project_gym.model.response.get.TraineeResponse;
import com.example.project_gym.model.response.get.TraineeTrainerResponse;
import com.example.project_gym.model.response.update.TraineeTrainersUpdateResponse;
import com.example.project_gym.model.response.update.TraineeUpdateResponse;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.security.AuthenticationGuard;
import com.example.project_gym.utilservices.authenticatedservices.PasswordChangeService;
import com.example.project_gym.utilservices.guestservices.password.PasswordGenerator;
import com.example.project_gym.utilservices.guestservices.username.UniqueUserNameGenerator;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TraineeService {

    private TraineeDAO traineeDao;

    private PasswordChangeService passwordChangeService;

    private UniqueUserNameGenerator nameGenerator;

    private PasswordGenerator passwordGenerator;

    private AuthenticationGuard authGuard;

    private TraineeMapper traineeMapper;

    private TrainingMapper trainingMapper;

    private TrainerMapper trainerMapper;

    private TrainerDAO trainerDao;

    public TraineeService(TraineeDAO traineeDao, PasswordChangeService passwordChangeService, UniqueUserNameGenerator nameGenerator, AuthenticationGuard authGuard, TraineeMapper traineeMapper, TrainingMapper trainingMapper, TrainerMapper trainerMapper, TrainerDAO trainerDao, PasswordGenerator passwordGenerator) {
        this.traineeDao = traineeDao;
        this.passwordChangeService = passwordChangeService;
        this.nameGenerator = nameGenerator;
        this.trainingMapper = trainingMapper;
        this.authGuard = authGuard;
        this.passwordGenerator = passwordGenerator;
        this.traineeMapper = traineeMapper;
        this.trainerMapper = trainerMapper;
        this.trainerDao = trainerDao;

    }

    @Transactional
    public TraineeCreateResponse create(TraineeCreateRequest traineeCreateRequest) {

        User user = new User();
        TraineeEntity traineeEntity = traineeMapper.toEntity(traineeCreateRequest);
        String username = nameGenerator.generateUnique(traineeCreateRequest.firstName(), traineeCreateRequest.lastName());

        boolean usernameAlreadyExists = traineeDao.getByUsername(username).isPresent() || trainerDao.getByUsername(username).isPresent();
        if (usernameAlreadyExists) {
            throw new DuplicateUsernameException("Username already exists: " + username);
        }

        user.setFirstName(traineeCreateRequest.firstName());
        user.setLastName(traineeCreateRequest.lastName());
        user.setUserName(username);
        user.setPassword(passwordGenerator.generatePassword());
        user.setActive(true);
        traineeEntity.setUser(user);
        return traineeMapper.toCreateResponse(traineeDao.create(traineeEntity));
    }

    @Transactional(readOnly = true)
    public TraineeResponse selectByUsername(TraineeRequest traineeRequest) {
        authGuard.requireAuthenticated();
        return traineeDao.getByUsername(traineeRequest.username())
                .map(traineeMapper::toResponse)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee with username " + traineeRequest.username() + " not found"));
    }

      @Transactional
    public TraineeUpdateResponse update(TraineeUpdateRequest traineeUpdateRequest) {
        authGuard.requireAuthenticated();

        TraineeEntity trainee = traineeDao.getByUsername(traineeUpdateRequest.username())
                .orElseThrow(() -> new TraineeNotFoundException(
                                "Trainee with username " + traineeUpdateRequest.username() + " not found"));

        traineeMapper.updateEntity(traineeUpdateRequest, trainee);
        trainee.getUser().setFirstName(traineeUpdateRequest.firstName());
        trainee.getUser().setLastName(traineeUpdateRequest.lastName());

        return traineeMapper.toUpdateResponse(traineeDao.update(trainee));
    }

    @Transactional
    public void activateDeactivate(UserActivationRequest updateRequest) {
        authGuard.requireAuthenticated();
        TraineeEntity traineeEntity = traineeDao.getByUsername(updateRequest.username())
                .orElseThrow(() -> new TraineeNotFoundException("Trainee with username " + updateRequest.username() + " not found"));

        if (traineeEntity.getUser().isActive() == updateRequest.isActive()) {
            throw new InactiveUserException("Trainee activation status is already " + updateRequest.isActive());
        }
        traineeMapper.updateActiveStatus(updateRequest, traineeEntity);
        traineeDao.update(traineeEntity);
    }

    public boolean deleteByUsername(TraineeRequest traineeRequest) {
        authGuard.requireAuthenticated();
        return traineeDao.deleteByUsername(traineeRequest.username());
    }

    @Transactional(readOnly = true)
    public List<SimpleTrainingResponse> getTrainings(TraineeTrainingsRequest trainingsRequest) {
        authGuard.requireAuthenticated();
        if (trainingsRequest.fromDate() != null && trainingsRequest.toDate() != null && trainingsRequest.fromDate().isAfter(trainingsRequest.toDate())) {
            throw new BusinessRuleException("fromDate cannot be after toDate");
        }
        List<TrainingEntity> trainings = traineeDao.getTrainings(
                trainingsRequest.traineeUsername(),
                trainingsRequest.fromDate(),
                trainingsRequest.toDate(),
                trainingsRequest.trainerName(),
                trainingsRequest.trainingType());
        return trainings.stream()
                .map(trainingMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TraineeTrainerResponse> getUnassignedTrainers(TraineeRequest traineeRequest) {
        authGuard.requireAuthenticated();
        return traineeDao.getUnassignedTrainers(traineeRequest.username()).stream()
                .map(trainerMapper::toTraineeTrainerResponse)
                .toList();
    }

    @Transactional
    public TraineeTrainersUpdateResponse updateTrainersList(TraineeTrainersUpdateRequest request) {
        authGuard.requireAuthenticated();

        TraineeEntity trainee = traineeDao.getByUsername(request.username())
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found"));

        long uniqueTrainers = request.trainers().stream()
                .map(trainer -> trainer.username().toLowerCase())
                .distinct()
                .count();
        if (uniqueTrainers != request.trainers().size()) {
            throw new InvalidTrainerAssignmentException("Trainer list contains duplicate usernames");
        }

        List<TrainerEntity> trainers = request.trainers()
                .stream()
                .map(trainerRequest -> trainerDao.getByUsername(trainerRequest.username())
                        .orElseThrow(() -> new InvalidTrainerAssignmentException("Trainer with username " + trainerRequest.username() + " not found")))
                .peek(trainer -> {
                    if (!trainer.getUser().isActive()) {
                        throw new InactiveUserException("Trainer " + trainer.getUser().getUserName() + " is inactive");
                    }
                }).toList();

        traineeDao.updateTrainersList(trainee, trainers);

        return new TraineeTrainersUpdateResponse(trainerMapper.toTraineeTrainerResponses(trainee.getTrainerEntities()));
    }

    @Transactional(readOnly = true)
    public TraineeEntity getById(Long id) {
        authGuard.requireAuthenticated();
        return traineeDao.getById(id)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found"));
    }




}