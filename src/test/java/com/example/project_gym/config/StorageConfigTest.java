package com.example.project_gym.config;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StorageConfigTest {

    @Test
    void beanFactories_shouldCreateExpectedBeans() {
        StorageConfig config = new StorageConfig();

        Map<Long, Trainer> trainerStorage = config.trainerStorage();
        Map<Long, Trainee> traineeStorage = config.traineeStorage();
        Map<Long, Training> trainingStorage = config.trainingStorage();
        ObjectMapper objectMapper = config.objectMapper();

        assertNotNull(trainerStorage);
        assertNotNull(traineeStorage);
        assertNotNull(trainingStorage);
        assertNotNull(objectMapper);

        assertTrue(trainerStorage.isEmpty());
        assertTrue(traineeStorage.isEmpty());
        assertTrue(trainingStorage.isEmpty());
    }
}

