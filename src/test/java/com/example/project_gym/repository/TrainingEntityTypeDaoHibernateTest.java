package com.example.project_gym.repository;

import com.example.project_gym.domain.entity.TrainingType;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingEntityTypeDaoHibernateTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private TypedQuery<TrainingType> query;

    private TrainingTypeDaoHibernate dao;

    @BeforeEach
    void setUp() {
        dao = new TrainingTypeDaoHibernate();
        ReflectionTestUtils.setField(dao, "entityManager", entityManager);
    }

    @Test
    void findByTrainingTypeName_shouldReturnOptional() {
        when(entityManager.createQuery(anyString(), eq(TrainingType.class))).thenReturn(query);
        when(query.setParameter("trainingTypeName", "CARDIO")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new TrainingType()));

        Optional<TrainingType> result = dao.findByTrainingTypeName("CARDIO");

        assertTrue(result.isPresent());
    }
}

