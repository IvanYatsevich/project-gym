package com.example.project_gym.service;

import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import com.example.project_gym.model.TrainingType;
import com.example.project_gym.model.dto.dtoin.PasswordChangeDto;
import com.example.project_gym.model.dto.dtoin.TrainerDtoIn;
import com.example.project_gym.model.dto.dtoin.TrainerTrainingsFilterDto;
import com.example.project_gym.model.dto.dtoupdate.TrainerUpdateDto;
import com.example.project_gym.repository.idao.ITrainerDAO;
import com.example.project_gym.repository.idao.ITrainingTypeDAO;
import com.example.project_gym.utilservices.authservices.PasswordChangeService;
import com.example.project_gym.utilservices.unauthservices.password.PasswordGenerator;
import com.example.project_gym.utilservices.unauthservices.username.UniqueUserNameGenerator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainerService {

    @Autowired
    private ITrainerDAO trainerDao;

    @Autowired
    private ITrainingTypeDAO trainingTypeDao;

    @Autowired
    private PasswordChangeService passwordChangeService;

    private UniqueUserNameGenerator nameGenerator;
    private PasswordGenerator passwordGenerator;


    @Autowired
    public void setUserNameGenerator(UniqueUserNameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    public Trainer create(TrainerDtoIn trainerDtoIn) {
        validateTrainerDtoInput(trainerDtoIn);

        TrainingType trainingType = resolveTrainingType(trainerDtoIn.specialization());

        Trainer trainer = new Trainer();
        trainer.setTrainingType(trainingType);

        com.example.project_gym.model.User user = new com.example.project_gym.model.User();
        user.setFirstName(trainerDtoIn.firstName());
        user.setLastName(trainerDtoIn.lastName());
        user.setUserName(nameGenerator.generateUnique(trainerDtoIn.firstName(), trainerDtoIn.lastName()));
        user.setPassword(passwordGenerator.generatePassword());
        user.setActive(true);
        
        trainer.setUser(user);
        trainerDao.create(trainer);
        return trainer;
    }

    @Transactional(readOnly = true)
    public Trainer selectByUsername(String username) {
        return trainerDao.selectByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer with username " + username + " not found"));
    }

    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        return trainerDao.selectByUsername(username)
                .map(trainer -> trainer.getUser().getPassword().equals(password))
                .orElseThrow(() -> new IllegalArgumentException("Username or password are invalid"));
    }

    public void changePassword(PasswordChangeDto passwordChangeDto) {
        passwordChangeService.changeTrainerPassword(passwordChangeDto);
    }

    public Trainer update(Long id, TrainerUpdateDto trainerUpdateDto) {
        validateTrainerUpdateDto(trainerUpdateDto);
        
        Trainer existingTrainer = trainerDao.selectById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));

        existingTrainer.getUser().setActive(trainerUpdateDto.isActive());
        existingTrainer.setTrainingType(resolveTrainingType(trainerUpdateDto.specialization()));

        trainerDao.update(existingTrainer);
        return existingTrainer;
    }

    public Trainer toggleActive(String username) {
        Trainer trainer = selectByUsername(username);
        trainer.getUser().setActive(!trainer.getUser().isActive());
        trainerDao.update(trainer);
        return trainer;
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainings(TrainerTrainingsFilterDto filterDto) {
        return trainerDao.getTrainings(
            filterDto.trainerUsername(),
            filterDto.fromDate(),
            filterDto.toDate(),
            filterDto.traineeName()
        );
    }

    @Transactional(readOnly = true)
    public Trainer select(Long id) {
        return trainerDao.selectById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));
    }

    private void validateTrainerDtoInput(TrainerDtoIn trainerDtoIn) {
        if (trainerDtoIn.firstName() == null || trainerDtoIn.firstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (trainerDtoIn.lastName() == null || trainerDtoIn.lastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (trainerDtoIn.specialization() == null || trainerDtoIn.specialization().trim().isEmpty()) {
            throw new IllegalArgumentException("Specialization is required");
        }
    }

    private void validateTrainerUpdateDto(TrainerUpdateDto trainerUpdateDto) {
        if (trainerUpdateDto == null) {
            throw new IllegalArgumentException("Trainer update data is required");
        }
        if (trainerUpdateDto.isActive() == null) {
            throw new IllegalArgumentException("isActive is required");
        }
        if (trainerUpdateDto.specialization() == null || trainerUpdateDto.specialization().trim().isEmpty()) {
            throw new IllegalArgumentException("Specialization is required");
        }
    }

    private TrainingType resolveTrainingType(String trainingTypeName) {
        return trainingTypeDao.findByTrainingTypeName(trainingTypeName)
                .orElseThrow(() -> new IllegalArgumentException("Training type not found: " + trainingTypeName));
    }
}
