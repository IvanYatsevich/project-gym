package com.example.project_gym.service;

import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.dto.dtoin.TrainerDtoIn;
import com.example.project_gym.model.dto.dtoupdate.TrainerUpdateDto;
import com.example.project_gym.repository.TrainerDaoImpl;
import com.example.project_gym.repository.idao.ITrainerDAO;
import com.example.project_gym.utilservices.PasswordGenerator;
import com.example.project_gym.utilservices.UniqueUserNameGenerator;
import com.example.project_gym.utilservices.UserIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerService {

    @Autowired
    private ITrainerDAO trainerDao;
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

    public Trainer create (TrainerDtoIn trainerDtoIn){
        Trainer trainer = new Trainer();
        Long id = idGenerator.generateId();
        trainer.setId(id);
        trainer.setFirstName(trainerDtoIn.firstName());
        trainer.setLastName(trainerDtoIn.lastName());
        trainer.setUserName(nameGenerator.generateUnique(trainerDtoIn.firstName(), trainerDtoIn.lastName()));
        trainer.setPassword(passwordGenerator.generatePassword());
        trainer.setActive(true);
        trainer.setTrainingType(trainerDtoIn.specialization());
        trainer.setUserId(id);
        trainerDao.create(trainer);
        return trainer;
    }

    public Trainer select (Long id){
        return trainerDao.selectById(id)
                .orElseThrow(() ->
                        new RuntimeException("Trainer with id " + id + " not found"));
    }

    public Trainer update(Long id, TrainerUpdateDto trainerUpdateDto) {
        Trainer existingTrainer = trainerDao.selectById(id)
                .orElseThrow(() ->
                        new RuntimeException("Trainer with id " + id + " not found"));

        if (trainerUpdateDto.isActive() != null) {
            existingTrainer.setActive(trainerUpdateDto.isActive());
        }

        if (trainerUpdateDto.specialization() != null) {
            existingTrainer.setTrainingType(trainerUpdateDto.specialization());
        }
        trainerDao.update(existingTrainer);
        return existingTrainer;
    }


}
