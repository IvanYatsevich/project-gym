package com.example.project_gym.service;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.domain.entity.TrainingType;
import com.example.project_gym.model.request.CreateTrainingRequest;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.repository.idao.TrainingDAO;
import com.example.project_gym.security.AuthenticationGuard;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingEntityServiceTest {

    @Mock
    private TrainingDAO trainingDao;
    @Mock
    private TrainerDAO trainerDao;
    @Mock
    private TraineeDAO traineeDao;
    @Mock
    private AuthenticationGuard authGuard;

    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        trainingService = new TrainingService();
        ReflectionTestUtils.setField(trainingService, "trainingDao", trainingDao);
        ReflectionTestUtils.setField(trainingService, "trainerDao", trainerDao);
        ReflectionTestUtils.setField(trainingService, "traineeDao", traineeDao);
        trainingService.setAuthenticationGuard(authGuard);
    }

    @Test
    void create_shouldCreateTraining() {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("CARDIO");
        TrainerEntity trainerEntity = new TrainerEntity();
        TraineeEntity traineeEntity = new TraineeEntity();
        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainerEntity));
        when(traineeDao.findById(2L)).thenReturn(Optional.of(traineeEntity));
        when(trainingDao.create(any(TrainingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainingEntity result = trainingService.create(new CreateTrainingRequest(1L, 2L, "Morning", type, LocalDateTime.now(), 45L));

        assertSame(trainerEntity, result.getTrainerEntity());
        assertSame(traineeEntity, result.getTraineeEntity());
        assertEquals("Morning", result.getTrainingName());
    }

    @Test
    void create_shouldThrowWhenTraineeMissing() {
        TrainingType type = new TrainingType();
        when(traineeDao.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> trainingService.create(new CreateTrainingRequest(1L, 2L, "Morning", type, LocalDateTime.now(), 45L)));
    }

    @Test
    void create_shouldThrowWhenTrainerMissing() {
        TrainingType type = new TrainingType();
        when(traineeDao.findById(2L)).thenReturn(Optional.of(new TraineeEntity()));
        when(trainerDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> trainingService.create(new CreateTrainingRequest(1L, 2L, "Morning", type, LocalDateTime.now(), 45L)));
    }

    @Test
    void create_shouldThrowWhenNameBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.create(new CreateTrainingRequest(1L, 2L, " ", new TrainingType(), LocalDateTime.now(), 45L)));
    }

    @Test
    void create_shouldThrowWhenDurationInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.create(new CreateTrainingRequest(1L, 2L, "Morning", new TrainingType(), LocalDateTime.now(), 0L)));
    }

    @Test
    void select_shouldThrowWhenMissing() {
        when(trainingDao.findById(10L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> trainingService.select(10L));
    }

    @Test
    void getAll_shouldDelegateToDao() {
        when(trainingDao.getAll()).thenReturn(List.of(new TrainingEntity(), new TrainingEntity()));
        assertEquals(2, trainingService.getAll().size());
    }

    @Test
    void allOperations_shouldThrowWhenUnauthenticated() {
        doThrow(new SecurityException("Authentication required")).when(authGuard).requireAuthenticated();

        assertAll(
                () -> assertThrows(SecurityException.class,
                        () -> trainingService.create(new CreateTrainingRequest(1L, 2L, "Morning", new TrainingType(), LocalDateTime.now(), 45L))),
                () -> assertThrows(SecurityException.class, () -> trainingService.select(10L)),
                () -> assertThrows(SecurityException.class, () -> trainingService.getAll())
        );
    }
}
