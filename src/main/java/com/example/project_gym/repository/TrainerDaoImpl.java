package com.example.project_gym.repository;

import com.example.project_gym.model.Trainer;
import com.example.project_gym.repository.idao.ITrainerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
@Repository
public class TrainerDaoImpl implements ITrainerDAO {

    private Map<Long, Trainer> storage;

    @Autowired
    @Qualifier("trainerStorage")
    public void setStorage(Map<Long, Trainer> trainerStorage) {
        this.storage = trainerStorage;
    }

    @Override
    public Trainer create(Trainer trainer) {
        storage.put(trainer.getId(), trainer);
        return trainer;
    }

    @Override
    public Optional<Trainer> selectById(Long id) {
        return Optional.ofNullable(storage.values().stream()
                .filter(trainer -> trainer.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Trainer with " + id + " is not found")));
    }

    @Override
    public Trainer update(Trainer trainer) {
        Long id = trainer.getId();

        if (id == null || !storage.containsKey(id)){
            throw new NoSuchElementException("Trainer with id " + id + " does not exist.");
        }

        storage.put(id, trainer);
        return trainer;
    }

    public boolean existsByUserName(String userName) {
        return storage.values().stream()
                .anyMatch(u -> u.getUserName() != null && u.getUserName().equalsIgnoreCase(userName));
    }
}
