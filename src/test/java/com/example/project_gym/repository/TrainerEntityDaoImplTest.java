package com.example.project_gym.repository;

import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerEntityDaoImplTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private TypedQuery<TrainerEntity> trainerQuery;
    @InjectMocks
    private TrainerDaoHibernate dao;
    @Mock
    private CriteriaBuilder cb;
    @Mock
    private CriteriaQuery<TrainingEntity> cq;
    @Mock
    private Root<TrainingEntity> root;
    @Mock
    private TypedQuery<TrainingEntity> typedQuery;
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
    private Predicate pAnd;
    @Mock
    private Path<?> trainerPath;
    @Mock
    private Path<?> userPath;
    @Mock
    private Path<String> userNamePath;
    @Mock
    private Path<LocalDateTime> trainingDatePath;
    @Mock
    private Path<?> traineePath;
    @Mock
    private Path<?> traineeUserPath;
    @Mock
    private Path<String> firstNamePath;
    @Mock
    private Path<String> lastNamePath;
    @Mock
    private Expression<String> firstNameLowerExpr;
    @Mock
    private Expression<String> lastNameLowerExpr;

    @BeforeEach
    void setUp() {
        dao = new TrainerDaoHibernate();
        ReflectionTestUtils.setField(dao, "entityManager", entityManager);
    }

    @BeforeEach
    void setupEntityManagerField() throws Exception {
        Field emField = TrainerDaoHibernate.class.getDeclaredField("entityManager");
        emField.setAccessible(true);
        emField.set(dao, entityManager);
    }

    @Test
    void create_shouldPersistAndReturnEntity() {
        TrainerEntity trainerEntity = new TrainerEntity();

        TrainerEntity result = dao.create(trainerEntity);

        assertSame(trainerEntity, result);
        verify(entityManager).persist(trainerEntity);
    }

    @Test
    void selectById_shouldReturnOptional() {
        TrainerEntity trainerEntity = new TrainerEntity();
        when(entityManager.find(TrainerEntity.class, 1L)).thenReturn(trainerEntity);

        Optional<TrainerEntity> result = dao.findById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void selectByUsername_shouldReturnFound() {
        when(entityManager.createQuery(anyString(), eq(TrainerEntity.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("userName", "ivan")).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(List.of(new TrainerEntity()));

        Optional<TrainerEntity> result = dao.findByUsername("ivan");

        assertTrue(result.isPresent());
    }

    @Test
    void deleteByUsername_shouldReturnFalseWhenMissing() {
        when(entityManager.createQuery(anyString(), eq(TrainerEntity.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("userName", "none")).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(Collections.emptyList());

        assertFalse(dao.deleteByUsername("none"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getTrainings_shouldReturnResults() {
        String trainerUsername = "trainer1";
        LocalDateTime fromDate = LocalDateTime.now().minusDays(1);
        LocalDateTime toDate = LocalDateTime.now();
        String traineeName = "john";
        List<TrainingEntity> expected = List.of(mock(TrainingEntity.class), mock(TrainingEntity.class));

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(TrainingEntity.class)).thenReturn(cq);
        when(cq.from(TrainingEntity.class)).thenReturn(root);

        when(root.get("trainerEntity")).thenReturn((Path) trainerPath);
        when(trainerPath.get("user")).thenReturn((Path) userPath);
        when(userPath.get("userName")).thenReturn((Path) userNamePath);

        when(cb.equal(userNamePath, trainerUsername)).thenReturn(pUsername);

        when(root.get("trainingDate")).thenReturn((Path) trainingDatePath);
        when(cb.greaterThanOrEqualTo(trainingDatePath, fromDate)).thenReturn(pFrom);
        when(cb.lessThanOrEqualTo(trainingDatePath, toDate)).thenReturn(pTo);

        when(root.get("traineeEntity")).thenReturn((Path) traineePath);
        when(traineePath.get("user")).thenReturn((Path) traineeUserPath);
        when(traineeUserPath.get("firstName")).thenReturn((Path) firstNamePath);
        when(traineeUserPath.get("lastName")).thenReturn((Path) lastNamePath);

        when(cb.lower(firstNamePath)).thenReturn(firstNameLowerExpr);
        when(cb.lower(lastNamePath)).thenReturn(lastNameLowerExpr);

        String pattern = "%john%";
        when(cb.like(firstNameLowerExpr, pattern)).thenReturn(pLikeFirst);
        when(cb.like(lastNameLowerExpr, pattern)).thenReturn(pLikeLast);
        when(cb.or(pLikeFirst, pLikeLast)).thenReturn(pOr);

        when(cb.and(any(Predicate[].class))).thenReturn(pAnd);
        when(cq.select(root)).thenReturn(cq);
        when(cq.where(pAnd)).thenReturn(cq);

        when(entityManager.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expected);

        List<TrainingEntity> actual = dao.getTrainings(trainerUsername, fromDate, toDate, traineeName);

        assertEquals(expected, actual);

        verify(entityManager).getCriteriaBuilder();
        verify(cb).createQuery(TrainingEntity.class);
        verify(cq).from(TrainingEntity.class);
        verify(entityManager).createQuery(cq);
        verify(typedQuery).getResultList();
    }
}
