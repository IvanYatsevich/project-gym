package com.example.project_gym.repository;

import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TrainerDaoImplTest {

    private TrainerDaoImpl dao;

    @BeforeEach
    void setUp() {
        dao = new TrainerDaoImpl();
        dao.setStorage(new HashMap<>());
    }

    @Test
    void createAndSelect_shouldWork() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setTrainingType(TrainingType.CARDIO);

        dao.create(trainer);

        Trainer actual = dao.selectById(1L).orElseThrow();
        assertSame(trainer, actual);
    }

    @Test
    void update_shouldReplaceExisting() {
        Trainer trainer = new Trainer();
        trainer.setId(2L);
        trainer.setTrainingType(TrainingType.CARDIO);
        dao.create(trainer);

        Trainer updated = new Trainer();
        updated.setId(2L);
        updated.setTrainingType(TrainingType.STRENGTH);

        dao.update(updated);

        assertEquals(TrainingType.STRENGTH, dao.selectById(2L).orElseThrow().getTrainingType());
    }

    @Test
    void update_shouldThrowWhenMissing() {
        Trainer trainer = new Trainer();
        trainer.setId(99L);

        assertThrows(NoSuchElementException.class, () -> dao.update(trainer));
    }

    @Test
    void existsByUserName_shouldIgnoreCase() {
        Trainer trainer = new Trainer();
        trainer.setId(3L);
        trainer.setUserName("John.Smith");
        dao.create(trainer);

        assertTrue(dao.existsByUserName("john.smith"));
        assertFalse(dao.existsByUserName("other"));
    }
}

