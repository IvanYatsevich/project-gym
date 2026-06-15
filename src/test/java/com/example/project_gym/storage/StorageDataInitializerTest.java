package com.example.project_gym.storage;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import com.example.project_gym.utilservices.UserIdGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StorageDataInitializerTest {

    @Mock
    private UserIdGenerator userIdGenerator;

    private StorageDataInitializer initializer;
    private Map<Long, Trainer> trainerStorage;
    private Map<Long, Trainee> traineeStorage;
    private Map<Long, Training> trainingStorage;

    @BeforeEach
    void setUp() {
        initializer = new StorageDataInitializer();
        trainerStorage = new HashMap<>();
        traineeStorage = new HashMap<>();
        trainingStorage = new HashMap<>();

        ReflectionTestUtils.setField(initializer, "initFile", "storage-init-data.json");
        ReflectionTestUtils.setField(initializer, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(initializer, "userIdGenerator", userIdGenerator);
        ReflectionTestUtils.setField(initializer, "trainerStorage", trainerStorage);
        ReflectionTestUtils.setField(initializer, "traineeStorage", traineeStorage);
        ReflectionTestUtils.setField(initializer, "trainingStorage", trainingStorage);
    }

    @Test
    void init_shouldLoadDataIntoStoragesAndSyncIdGenerator() throws Exception {
        initializer.init();

        assertEquals(2, trainerStorage.size());
        assertEquals(2, traineeStorage.size());
        assertEquals(2, trainingStorage.size());

        assertEquals("Ivan", trainerStorage.get(1L).getFirstName());
        assertEquals("New York", traineeStorage.get(3L).getAddress());
        assertEquals("Morning Cardio", trainingStorage.get(1L).getTrainingName());

        verify(userIdGenerator).setInitialValue(4L);
    }
}

