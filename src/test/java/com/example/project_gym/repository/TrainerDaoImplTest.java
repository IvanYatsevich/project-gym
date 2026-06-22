package com.example.project_gym.repository;

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
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerDaoImplTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private TypedQuery<Trainer> trainerQuery;
    @Mock
    private TypedQuery<Training> trainingQuery;

    private TrainerDaoHibernate dao;

    @BeforeEach
    void setUp() {
        dao = new TrainerDaoHibernate();
        ReflectionTestUtils.setField(dao, "entityManager", entityManager);
    }

    @Test
    void create_shouldPersistAndReturnEntity() {
        Trainer trainer = new Trainer();

        Trainer result = dao.create(trainer);

        assertSame(trainer, result);
        verify(entityManager).persist(trainer);
    }

    @Test
    void selectById_shouldReturnOptional() {
        Trainer trainer = new Trainer();
        when(entityManager.find(Trainer.class, 1L)).thenReturn(trainer);

        Optional<Trainer> result = dao.selectById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void selectByUsername_shouldReturnFound() {
        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("userName", "ivan")).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(List.of(new Trainer()));

        Optional<Trainer> result = dao.selectByUsername("ivan");

        assertTrue(result.isPresent());
    }

    @Test
    void deleteByUsername_shouldReturnFalseWhenMissing() {
        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("userName", "none")).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(Collections.emptyList());

        assertFalse(dao.deleteByUsername("none"));
    }

    @Test
    void getTrainings_shouldReturnResults() {
        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(trainingQuery);
        when(trainingQuery.setParameter(eq("trainerUsername"), eq("ivan"))).thenReturn(trainingQuery);
        Date now = new Date();
        when(trainingQuery.setParameter("fromDate", now)).thenReturn(trainingQuery);
        when(trainingQuery.getResultList()).thenReturn(List.of(new Training()));

        List<Training> result = dao.getTrainings("ivan", now, null, null);

        assertEquals(1, result.size());
    }
}
