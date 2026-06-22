package com.example.project_gym.repository;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeDaoImplTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private TypedQuery<Trainee> traineeQuery;
    @Mock
    private TypedQuery<Training> trainingQuery;
    @Mock
    private TypedQuery<Trainer> trainerQuery;

    private TraineeDaoHibernate dao;

    @BeforeEach
    void setUp() {
        dao = new TraineeDaoHibernate();
        ReflectionTestUtils.setField(dao, "entityManager", entityManager);
    }

    @Test
    void create_shouldPersistAndReturnEntity() {
        Trainee trainee = new Trainee();

        Trainee result = dao.create(trainee);

        assertSame(trainee, result);
        verify(entityManager).persist(trainee);
    }

    @Test
    void delete_shouldThrowWhenMissing() {
        when(entityManager.find(Trainee.class, 10L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> dao.delete(10L));
    }

    @Test
    void selectByUsername_shouldReturnOptional() {
        when(entityManager.createQuery(anyString(), eq(Trainee.class))).thenReturn(traineeQuery);
        when(traineeQuery.setParameter("userName", "hulk")).thenReturn(traineeQuery);
        when(traineeQuery.getResultList()).thenReturn(List.of(new Trainee()));

        Optional<Trainee> result = dao.selectByUsername("hulk");

        assertTrue(result.isPresent());
    }

    @Test
    void deleteByUsername_shouldReturnFalseWhenMissing() {
        when(entityManager.createQuery(anyString(), eq(Trainee.class))).thenReturn(traineeQuery);
        when(traineeQuery.setParameter("userName", "none")).thenReturn(traineeQuery);
        when(traineeQuery.getResultList()).thenReturn(Collections.emptyList());

        assertFalse(dao.deleteByUsername("none"));
    }

    @Test
    void findUnassignedTrainers_shouldReturnList() {
        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("traineeUsername", "hulk")).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(List.of(new Trainer()));

        List<Trainer> result = dao.findUnassignedTrainers("hulk");

        assertEquals(1, result.size());
    }
}
