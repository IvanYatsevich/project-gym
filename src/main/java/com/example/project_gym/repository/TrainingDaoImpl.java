package com.example.project_gym.repository;

import com.example.project_gym.model.Training;
import com.example.project_gym.repository.idao.ITrainingDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
@Repository
public class TrainingDaoImpl implements ITrainingDAO {

    private Map<Long, Training> trainingStorage;
    private long seq = 0L;


    @Autowired
    @Qualifier("trainingStorage")
    public void setStorage(Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }


    @Override
    public Training create(Training training) {
        long key = ++seq;
        trainingStorage.put(key, training);
        return training;
    }

    @Override
    public Optional<Training> getById(Long id) {
        return Optional.ofNullable(trainingStorage.get(id));
    }


}
