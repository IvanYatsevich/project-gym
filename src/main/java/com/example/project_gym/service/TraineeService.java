package com.example.project_gym.service;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import com.example.project_gym.model.User;
import com.example.project_gym.model.dto.dtoin.PasswordChangeDto;
import com.example.project_gym.model.dto.dtoin.TraineeDtoIn;
import com.example.project_gym.model.dto.dtoin.TrainingFilterDto;
import com.example.project_gym.model.dto.dtoupdate.TraineeUpdateDto;
import com.example.project_gym.repository.idao.ITraineeDAO;
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
public class TraineeService {

    @Autowired
    private ITraineeDAO traineeDao;

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

    public Trainee create(TraineeDtoIn traineeDtoIn) {
        validateTraineeDtoInput(traineeDtoIn);

        Trainee trainee = new Trainee();
        User user = new User();

        user.setFirstName(traineeDtoIn.firstName());
        user.setLastName(traineeDtoIn.lastName());
        user.setUserName(nameGenerator.generateUnique(traineeDtoIn.firstName(), traineeDtoIn.lastName()));
        user.setPassword(passwordGenerator.generatePassword());
        user.setActive(true);

        trainee.setDateOfBirth(traineeDtoIn.dateOfBirth());
        trainee.setAddress(traineeDtoIn.address());
        trainee.setUser(user);
        traineeDao.create(trainee);
        return trainee;
    }

    @Transactional(readOnly = true)
    public Trainee selectByUsername(String username) {
        return traineeDao.selectByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee with username " + username + " not found"));
    }

    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        return traineeDao.selectByUsername(username)
                .map(trainee -> trainee.getUser().getPassword().equals(password))
                .orElseThrow(() -> new IllegalArgumentException("Username or password are invalid"));
    }

    public void changePassword(PasswordChangeDto passwordChangeDto) {
        passwordChangeService.changeTraineePassword(passwordChangeDto);
    }

    public Trainee update(Long id, TraineeUpdateDto traineeUpdateDto) {
        validateTraineeUpdateDto(traineeUpdateDto);
        Trainee existingTrainee = traineeDao.selectById(id).orElseThrow(() -> new EntityNotFoundException("Trainee not found"));

        existingTrainee.getUser().setActive(traineeUpdateDto.isActive());
        existingTrainee.setAddress(traineeUpdateDto.address());
        existingTrainee.setDateOfBirth(traineeUpdateDto.dateOfBirth());

        traineeDao.update(existingTrainee);
        return existingTrainee;
    }

    public Trainee toggleActive(String username) {
        Trainee trainee = selectByUsername(username);
        trainee.getUser().setActive(!trainee.getUser().isActive());
        traineeDao.update(trainee);
        return trainee;
    }

    public boolean deleteByUsername(String username) {
        return traineeDao.deleteByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainings(TrainingFilterDto filterDto) {
        return traineeDao.getTrainings(
            filterDto.traineeUsername(),
            filterDto.fromDate(),
            filterDto.toDate(),
            filterDto.trainerName(),
            filterDto.trainingType()
        );
    }

    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainers(String traineeUsername) {
        return traineeDao.findUnassignedTrainers(traineeUsername);
    }

    public void updateTrainersList(String traineeUsername, List<Trainer> trainers) {
        Trainee trainee = selectByUsername(traineeUsername);
        traineeDao.updateTrainersList(trainee, trainers);
    }

    @Transactional(readOnly = true)
    public Trainee select(Long id) {
        return traineeDao.selectById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
    }

    public boolean delete(Long id) {
        return traineeDao.delete(id);
    }

    private void validateTraineeDtoInput(TraineeDtoIn traineeDtoIn) {
        if (traineeDtoIn.firstName() == null || traineeDtoIn.firstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (traineeDtoIn.lastName() == null || traineeDtoIn.lastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }

    private void validateTraineeUpdateDto(TraineeUpdateDto traineeUpdateDto) {
        if (traineeUpdateDto == null) {
            throw new IllegalArgumentException("Trainee update data is required");
        }
        if (traineeUpdateDto.isActive() == null) {
            throw new IllegalArgumentException("isActive is required");
        }
    }

}
