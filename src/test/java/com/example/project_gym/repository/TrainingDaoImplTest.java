package com.example.project_gym.repository;

import com.example.project_gym.model.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingDaoImplTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private TypedQuery<Training> query;

    private TrainingDaoHibernate dao;

    @BeforeEach
    void setUp() {
        dao = new TrainingDaoHibernate();
        ReflectionTestUtils.setField(dao, "entityManager", entityManager);
    }

    @Test
    void create_shouldPersist() {
        Training training = new Training();

        Training result = dao.create(training);

        assertSame(training, result);
        verify(entityManager).persist(training);
    }

    @Test
    void getById_shouldReturnOptional() {
        when(entityManager.find(Training.class, 2L)).thenReturn(new Training());

        Optional<Training> result = dao.getById(2L);

        assertTrue(result.isPresent());
    }

    @Test
    void getAll_shouldReturnList() {
        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Training(), new Training()));

        List<Training> result = dao.getAll();

        assertEquals(2, result.size());
    }
}
