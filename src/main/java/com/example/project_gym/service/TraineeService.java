package com.example.project_gym.service;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.dto.dtoin.TraineeDtoIn;
import com.example.project_gym.model.dto.dtoupdate.TraineeUpdateDto;
import com.example.project_gym.repository.TraineeDaoImpl;
import com.example.project_gym.utilservices.PasswordGenerator;
import com.example.project_gym.utilservices.UniqueUserNameGenerator;
import com.example.project_gym.utilservices.UserIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeService {

    @Autowired
    private TraineeDaoImpl traineeDao;
    private UserIdGenerator idGenerator;
    private UniqueUserNameGenerator nameGenerator;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setIdGenerator(UserIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Autowired
    public void setUserNameGenerator(UniqueUserNameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    public Trainee create(TraineeDtoIn traineeDtoIn){
        Trainee trainee = new Trainee();
        Long id = idGenerator.generateId();
        trainee.setId(id);
        trainee.setFirstName(traineeDtoIn.firstName());
        trainee.setLastName(traineeDtoIn.lastName());
        trainee.setUserName(nameGenerator.generateUnique(traineeDtoIn.firstName(), traineeDtoIn.lastName()));
        trainee.setPassword(passwordGenerator.generatePassword());
        trainee.setActive(true);
        trainee.setDateOfBirth(traineeDtoIn.dateOfBirth());
        trainee.setAddress(traineeDtoIn.address());
        trainee.setUserId(id);
        traineeDao.create(trainee);
        return trainee;
    }



    public Trainee select (Long id){
        return traineeDao.selectById(id)
                .orElseThrow(() ->
                        new RuntimeException("Trainee with id " + id + " not found"));
    }

    public Trainee update(Long id, TraineeUpdateDto traineeUpdateDto) {
        Trainee existingTrainee = traineeDao.selectById(id)
                .orElseThrow(() ->
                        new RuntimeException("Trainee with id " + id + " not found"));

        if (traineeUpdateDto.isActive() != null) {
            existingTrainee.setActive(traineeUpdateDto.isActive());
        }

        if (traineeUpdateDto.address() != null) {
            existingTrainee.setAddress(traineeUpdateDto.address());
        }
        traineeDao.update(existingTrainee);
        return existingTrainee;
    }

    public boolean delete(Long id) {
        Trainee existingTrainee = traineeDao.selectById(id)
                .orElseThrow(() ->
                        new RuntimeException("Trainee with id " + id + " not found"));
        return traineeDao.delete(existingTrainee.getId());
    }


}
