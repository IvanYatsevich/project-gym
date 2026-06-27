package com.example.project_gym.repository;

import com.example.project_gym.domain.entity.TrainingEntity;
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
class TrainingEntityDaoImplTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private TypedQuery<TrainingEntity> query;

    private TrainingDaoHibernate dao;

    @BeforeEach
    void setUp() {
        dao = new TrainingDaoHibernate();
        ReflectionTestUtils.setField(dao, "entityManager", entityManager);
    }

    @Test
    void create_shouldPersist() {
        TrainingEntity trainingEntity = new TrainingEntity();

        TrainingEntity result = dao.create(trainingEntity);

        assertSame(trainingEntity, result);
        verify(entityManager).persist(trainingEntity);
    }

    @Test
    void getById_shouldReturnOptional() {
        when(entityManager.find(TrainingEntity.class, 2L)).thenReturn(new TrainingEntity());

        Optional<TrainingEntity> result = dao.findById(2L);

        assertTrue(result.isPresent());
    }

    @Test
    void getAll_shouldReturnList() {
        when(entityManager.createQuery(anyString(), eq(TrainingEntity.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new TrainingEntity(), new TrainingEntity()));

        List<TrainingEntity> result = dao.getAll();

        assertEquals(2, result.size());
    }
}
