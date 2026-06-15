package com.example.project_gym.repository;

import com.example.project_gym.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class TrainingDaoImplTest {

    private TrainingDaoImpl dao;

    @BeforeEach
    void setUp() {
        dao = new TrainingDaoImpl();
        dao.setStorage(new HashMap<>());
    }

    @Test
    void create_shouldUseSequentialKeys() {
        Training t1 = new Training();
        Training t2 = new Training();

        dao.create(t1);
        dao.create(t2);

        assertSame(t1, dao.getById(1L).orElseThrow());
        assertSame(t2, dao.getById(2L).orElseThrow());
        assertTrue(dao.getById(3L).isEmpty());
    }
}

