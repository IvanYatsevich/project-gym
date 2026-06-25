package com.example.project_gym.service;

import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.domain.entity.TrainingType;
import com.example.project_gym.model.request.PasswordChangeRequest;
import com.example.project_gym.model.request.CreateTrainerRequest;
import com.example.project_gym.model.request.TrainerTrainingsFilterRequest;
import com.example.project_gym.model.request.UpdateTrainerRequest;
import com.example.project_gym.domain.entity.User;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.repository.idao.TrainingTypeDAO;
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
public class TrainerService {

    @Autowired
    private TrainerDAO trainerDao;

    @Autowired
    private TrainingTypeDAO trainingTypeDao;

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
    public TrainerEntity create(CreateTrainerRequest createTrainerRequest) {
        validateTrainerDtoInput(createTrainerRequest);

        TrainingType trainingType = resolveTrainingType(createTrainerRequest.specialization());

        TrainerEntity trainerEntity = new TrainerEntity();
        trainerEntity.setTrainingType(trainingType);

        User user = new User();
        user.setFirstName(createTrainerRequest.firstName());
        user.setLastName(createTrainerRequest.lastName());
        user.setUserName(nameGenerator.generateUnique(createTrainerRequest.firstName(), createTrainerRequest.lastName()));
        user.setPassword(passwordGenerator.generatePassword());
        user.setActive(true);
        
        trainerEntity.setUser(user);
        trainerDao.create(trainerEntity);
        return trainerEntity;
    }

    @Transactional(readOnly = true)
    public TrainerEntity selectByUsername(String username) {
        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer with username " + username + " not found"));
    }

//    @Transactional(readOnly = true)
//    public boolean authenticate(String username, String password) {
//        return trainerDao.selectByUsername(username)
//                .map(trainer -> trainer.getUser().getPassword().equals(password))
//                .orElseThrow(() -> new IllegalArgumentException("Username or password are invalid"));
//    }

    @Transactional
    public void changePassword(PasswordChangeRequest passwordChangeRequest) {
        passwordChangeService.changeTrainerPassword(passwordChangeRequest);
    }
    @Transactional
    public TrainerEntity update(Long id, UpdateTrainerRequest updateTrainerRequest) {
        validateTrainerUpdateDto(updateTrainerRequest);
        
        TrainerEntity existingTrainerEntity = trainerDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));

        existingTrainerEntity.getUser().setActive(updateTrainerRequest.isActive());
        existingTrainerEntity.setTrainingType(resolveTrainingType(updateTrainerRequest.specialization()));

        trainerDao.update(existingTrainerEntity);
        return existingTrainerEntity;
    }
    @Transactional
    public TrainerEntity toggleActive(String username) {
        TrainerEntity trainerEntity = selectByUsername(username);
        trainerEntity.getUser().setActive(!trainerEntity.getUser().isActive());
        trainerDao.update(trainerEntity);
        return trainerEntity;
    }

    @Transactional(readOnly = true)
    public List<TrainingEntity> getTrainings(TrainerTrainingsFilterRequest filterDto) {
        return trainerDao.getTrainings(
            filterDto.trainerUsername(),
            filterDto.fromDate(),
            filterDto.toDate(),
            filterDto.traineeName()
        );
    }

    @Transactional(readOnly = true)
    public TrainerEntity select(Long id) {
        return trainerDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));
    }

    private void validateTrainerDtoInput(CreateTrainerRequest createTrainerRequest) {
        if (createTrainerRequest.firstName() == null || createTrainerRequest.firstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (createTrainerRequest.lastName() == null || createTrainerRequest.lastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (createTrainerRequest.specialization() == null || createTrainerRequest.specialization().trim().isEmpty()) {
            throw new IllegalArgumentException("Specialization is required");
        }
    }

    private void validateTrainerUpdateDto(UpdateTrainerRequest updateTrainerRequest) {
        if (updateTrainerRequest == null) {
            throw new IllegalArgumentException("Trainer update data is required");
        }
        if (updateTrainerRequest.isActive() == null) {
            throw new IllegalArgumentException("isActive is required");
        }
        if (updateTrainerRequest.specialization() == null || updateTrainerRequest.specialization().trim().isEmpty()) {
            throw new IllegalArgumentException("Specialization is required");
        }
    }

    private TrainingType resolveTrainingType(String trainingTypeName) {
        return trainingTypeDao.findByTrainingTypeName(trainingTypeName)
                .orElseThrow(() -> new IllegalArgumentException("Training type not found: " + trainingTypeName));
    }
}
