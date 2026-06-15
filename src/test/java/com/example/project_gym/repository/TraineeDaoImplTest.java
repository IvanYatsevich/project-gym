package com.example.project_gym.repository;

import com.example.project_gym.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TraineeDaoImplTest {

    private TraineeDaoImpl dao;

    @BeforeEach
    void setUp() {
        dao = new TraineeDaoImpl();
        dao.setStorage(new HashMap<>());
    }

    @Test
    void createSelectUpdateDelete_shouldWork() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setAddress("Old");
        trainee.setUserName("user.one");

        dao.create(trainee);
        assertEquals("Old", dao.selectById(1L).orElseThrow().getAddress());

        trainee.setAddress("New");
        dao.update(trainee);
        assertEquals("New", dao.selectById(1L).orElseThrow().getAddress());

        assertTrue(dao.delete(1L));
        assertThrows(NoSuchElementException.class, () -> dao.selectById(1L));
    }

    @Test
    void delete_shouldThrowWhenMissing() {
        assertThrows(NoSuchElementException.class, () -> dao.delete(777L));
    }

    @Test
    void existsByUserName_shouldIgnoreCase() {
        Trainee trainee = new Trainee();
        trainee.setId(2L);
        trainee.setUserName("Jane.Doe");
        dao.create(trainee);

        assertTrue(dao.existsByUserName("jane.doe"));
        assertFalse(dao.existsByUserName("none"));
    }
}

