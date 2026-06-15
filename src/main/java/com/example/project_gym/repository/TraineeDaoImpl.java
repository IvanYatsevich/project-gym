package com.example.project_gym.repository;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.repository.idao.ITraineeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
@Repository
public class TraineeDaoImpl implements ITraineeDAO {
    private Map<Long, Trainee> storage;

    @Autowired
    @Qualifier("traineeStorage")
    public void setStorage(Map<Long, Trainee> traineeStorage) {
        this.storage = traineeStorage;
    }

    @Override
    public Trainee create(Trainee trainee) {
        storage.put(trainee.getId(), trainee);
        return trainee;
    }

    @Override
    public boolean delete(Long id) {
        if (id == null || !storage.containsKey(id)) {
            throw new NoSuchElementException("Trainee with id " + id + " does not exist.");
        }
        storage.remove(id);
        return true;
    }

    @Override
    public Optional<Trainee> selectById(Long id) {
        return Optional.ofNullable(storage.values().stream()
                .filter(trainee -> trainee.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Trainee with " + id + " is not found")));
    }

    @Override
    public Trainee update(Trainee trainee) {
        Long id = trainee.getId();

        if (id == null || !storage.containsKey(id)){
            throw new NoSuchElementException("Trainee with id " + id + " does not exist.");
        }

        storage.put(id, trainee);
        return trainee;
    }

    public boolean existsByUserName(String userName) {
        return storage.values().stream()
                .anyMatch(u -> u.getUserName() != null && u.getUserName().equalsIgnoreCase(userName));
    }
}
