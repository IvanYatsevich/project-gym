package com.example.project_gym.service;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.domain.entity.User;
import com.example.project_gym.model.request.PasswordChangeRequest;
import com.example.project_gym.model.request.CreateTraineeRequest;
import com.example.project_gym.model.request.TraineeTrainingsFilterRequest;
import com.example.project_gym.model.request.UpdateTraineeRequest;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.utilservices.authenticatedservices.PasswordChangeService;
import com.example.project_gym.utilservices.guestservices.password.PasswordGenerator;
import com.example.project_gym.utilservices.guestservices.username.UniqueUserNameGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TraineeService {

    @Autowired
    private TraineeDAO traineeDao;

    @Autowired
    private PasswordChangeService passwordChangeService;

    private UniqueUserNameGenerator nameGenerator;
    @Setter
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setUserNameGenerator(UniqueUserNameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    @Transactional
    public TraineeEntity create(CreateTraineeRequest createTraineeRequest) {
        validateTraineeDtoInput(createTraineeRequest);

        TraineeEntity traineeEntity = new TraineeEntity();
        User user = new User();

        user.setFirstName(createTraineeRequest.firstName());
        user.setLastName(createTraineeRequest.lastName());
        user.setUserName(nameGenerator.generateUnique(createTraineeRequest.firstName(), createTraineeRequest.lastName()));
        user.setPassword(passwordGenerator.generatePassword());
        user.setActive(true);

        traineeEntity.setDateOfBirth(createTraineeRequest.dateOfBirth());
        traineeEntity.setAddress(createTraineeRequest.address());
        traineeEntity.setUser(user);
        traineeDao.create(traineeEntity);
        return traineeEntity;
    }

    @Transactional(readOnly = true)
    public TraineeEntity selectByUsername(String username) {
        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee with username " + username + " not found"));
    }

//    @Transactional(readOnly = true)
//    public boolean authenticate(String username, String password) {
//        return traineeDao.selectByUsername(username)
//                .map(trainee -> trainee.getUser().getPassword().equals(password))
//                .orElseThrow(() -> new IllegalArgumentException("Username or password are invalid"));
//    }

    public void changePassword(PasswordChangeRequest passwordChangeRequest) {
        passwordChangeService.changeTraineePassword(passwordChangeRequest);
    }
    @Transactional
    public TraineeEntity update(Long id, UpdateTraineeRequest updateTraineeRequest) {
        validateTraineeUpdateDto(updateTraineeRequest);
        TraineeEntity existingTraineeEntity = traineeDao.findById(id).orElseThrow(() -> new EntityNotFoundException("Trainee not found"));

        existingTraineeEntity.getUser().setActive(updateTraineeRequest.isActive());
        existingTraineeEntity.setAddress(updateTraineeRequest.address());
        existingTraineeEntity.setDateOfBirth(updateTraineeRequest.dateOfBirth());

        traineeDao.update(existingTraineeEntity);
        return existingTraineeEntity;
    }
    @Transactional
    public TraineeEntity toggleActive(String username) {
        TraineeEntity traineeEntity = selectByUsername(username);
        traineeEntity.getUser().setActive(!traineeEntity.getUser().isActive());
        traineeDao.update(traineeEntity);
        return traineeEntity;
    }

    public boolean deleteByUsername(String username) {
        return traineeDao.deleteByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<TrainingEntity> getTrainings(TraineeTrainingsFilterRequest filterDto) {
        return traineeDao.getTrainings(
            filterDto.traineeUsername(),
            filterDto.fromDate(),
            filterDto.toDate(),
            filterDto.trainerName(),
            filterDto.trainingType()
        );
    }

    @Transactional(readOnly = true)
    public List<TrainerEntity> getUnassignedTrainers(String traineeUsername) {
        return traineeDao.findUnassignedTrainers(traineeUsername);
    }
    @Transactional
    public void updateTrainersList(String traineeUsername, List<TrainerEntity> trainerEntities) {
        TraineeEntity traineeEntity = selectByUsername(traineeUsername);
        traineeDao.updateTrainersList(traineeEntity, trainerEntities);
    }

    @Transactional(readOnly = true)
    public TraineeEntity select(Long id) {
        return traineeDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
    }

    @Transactional
    public boolean delete(Long id) {
        return traineeDao.delete(id);
    }

    private void validateTraineeDtoInput(CreateTraineeRequest createTraineeRequest) {
        if (createTraineeRequest.firstName() == null || createTraineeRequest.firstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (createTraineeRequest.lastName() == null || createTraineeRequest.lastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }

    private void validateTraineeUpdateDto(UpdateTraineeRequest updateTraineeRequest) {
        if (updateTraineeRequest == null) {
            throw new IllegalArgumentException("Trainee update data is required");
        }
        if (updateTraineeRequest.isActive() == null) {
            throw new IllegalArgumentException("isActive is required");
        }
    }

}
