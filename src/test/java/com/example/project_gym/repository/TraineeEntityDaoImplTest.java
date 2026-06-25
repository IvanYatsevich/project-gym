package com.example.project_gym.repository;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeEntityDaoImplTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private TypedQuery<TraineeEntity> traineeQuery;
    @Mock
    private TypedQuery<TrainingEntity> trainingQuery;
    @Mock
    private TypedQuery<TrainerEntity> trainerQuery;
    @Mock
    private CriteriaBuilder cb;
    @Mock
    private CriteriaQuery<TrainingEntity> cq;
    @Mock
    private Root<TrainingEntity> root;
    @Mock
    private Predicate pUsername;
    @Mock
    private Predicate pFrom;
    @Mock
    private Predicate pTo;
    @Mock
    private Predicate pLikeFirst;
    @Mock
    private Predicate pLikeLast;
    @Mock
    private Predicate pOr;
    @Mock
    private Predicate pType;
    @Mock
    private Predicate pAnd;
    @Mock
    private Path<?> traineePath;
    @Mock
    private Path<?> traineeUserPath;
    @Mock
    private Path<String> userNamePath;
    @Mock
    private Path<Date> trainingDatePath;
    @Mock
    private Path<?> trainerPath;
    @Mock
    private Path<?> trainerUserPath;
    @Mock
    private Path<String> firstNamePath;
    @Mock
    private Path<String> lastNamePath;
    @Mock
    private Expression<String> firstNameLowerExpr;
    @Mock
    private Expression<String> lastNameLowerExpr;
    @Mock
    private Path<?> trainingTypePath;
    @Mock
    private Path<String> trainingTypeNamePath;

    private TraineeDaoHibernate dao;

    @BeforeEach
    void setUp() {
        dao = new TraineeDaoHibernate();
        ReflectionTestUtils.setField(dao, "entityManager", entityManager);
    }

    @Test
    void create_shouldPersistAndReturnEntity() {
        TraineeEntity traineeEntity = new TraineeEntity();

        TraineeEntity result = dao.create(traineeEntity);

        assertSame(traineeEntity, result);
        verify(entityManager).persist(traineeEntity);
    }

    @Test
    void delete_shouldThrowWhenMissing() {
        when(entityManager.find(TraineeEntity.class, 10L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> dao.delete(10L));
    }

    @Test
    void selectByUsername_shouldReturnOptional() {
        when(entityManager.createQuery(anyString(), eq(TraineeEntity.class))).thenReturn(traineeQuery);
        when(traineeQuery.setParameter("userName", "hulk")).thenReturn(traineeQuery);
        when(traineeQuery.getResultList()).thenReturn(List.of(new TraineeEntity()));

        Optional<TraineeEntity> result = dao.findByUsername("hulk");

        assertTrue(result.isPresent());
    }

    @Test
    void deleteByUsername_shouldReturnFalseWhenMissing() {
        when(entityManager.createQuery(anyString(), eq(TraineeEntity.class))).thenReturn(traineeQuery);
        when(traineeQuery.setParameter("userName", "none")).thenReturn(traineeQuery);
        when(traineeQuery.getResultList()).thenReturn(Collections.emptyList());

        assertFalse(dao.deleteByUsername("none"));
    }

    @Test
    void findUnassignedTrainers_shouldReturnList() {
        when(entityManager.createQuery(anyString(), eq(TrainerEntity.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("traineeUsername", "hulk")).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(List.of(new TrainerEntity()));

        List<TrainerEntity> result = dao.findUnassignedTrainers("hulk");

        assertEquals(1, result.size());
    }

    @Test
    void getTrainings_shouldReturnResults() {
        String traineeUsername = "hulk";
        Date fromDate = new Date(System.currentTimeMillis() - 86400000L);
        Date toDate = new Date();
        String trainerName = "john";
        String trainingType = "Cardio";

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(TrainingEntity.class)).thenReturn(cq);
        when(cq.from(TrainingEntity.class)).thenReturn(root);

        doReturn(traineePath).when(root).get("traineeEntity");
        doReturn(traineeUserPath).when(traineePath).get("user");
        doReturn(userNamePath).when(traineeUserPath).get("userName");
        when(cb.equal(userNamePath, traineeUsername)).thenReturn(pUsername);

        doReturn(trainingDatePath).when(root).get("trainingDate");
        when(cb.greaterThanOrEqualTo(trainingDatePath, fromDate)).thenReturn(pFrom);
        when(cb.lessThanOrEqualTo(trainingDatePath, toDate)).thenReturn(pTo);

        doReturn(trainerPath).when(root).get("trainerEntity");
        doReturn(trainerUserPath).when(trainerPath).get("user");
        doReturn(firstNamePath).when(trainerUserPath).get("firstName");
        doReturn(lastNamePath).when(trainerUserPath).get("lastName");
        when(cb.lower(firstNamePath)).thenReturn(firstNameLowerExpr);
        when(cb.lower(lastNamePath)).thenReturn(lastNameLowerExpr);
        when(cb.like(firstNameLowerExpr, "%john%")).thenReturn(pLikeFirst);
        when(cb.like(lastNameLowerExpr, "%john%")).thenReturn(pLikeLast);
        when(cb.or(pLikeFirst, pLikeLast)).thenReturn(pOr);

        doReturn(trainingTypePath).when(root).get("trainingType");
        doReturn(trainingTypeNamePath).when(trainingTypePath).get("trainingTypeName");
        when(cb.equal(trainingTypeNamePath, trainingType)).thenReturn(pType);

        when(cb.and(any(Predicate[].class))).thenReturn(pAnd);
        when(cq.select(root)).thenReturn(cq);
        when(cq.where(pAnd)).thenReturn(cq);
        when(entityManager.createQuery(cq)).thenReturn(trainingQuery);
        when(trainingQuery.getResultList()).thenReturn(List.of(new TrainingEntity()));

        List<TrainingEntity> result = dao.getTrainings(traineeUsername, fromDate, toDate, trainerName, trainingType);

        assertEquals(1, result.size());
        verify(entityManager).getCriteriaBuilder();
        verify(cb).createQuery(TrainingEntity.class);
        verify(cq).from(TrainingEntity.class);
        verify(entityManager).createQuery(cq);
        verify(trainingQuery).getResultList();
    }
}
