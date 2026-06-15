package com.example.project_gym.service;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import com.example.project_gym.model.TrainingType;
import com.example.project_gym.model.dto.dtoin.TrainingDtoIn;
import com.example.project_gym.repository.TraineeDaoImpl;
import com.example.project_gym.repository.TrainerDaoImpl;
import com.example.project_gym.repository.TrainingDaoImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingDaoImpl trainingDao;
    @Mock
    private TrainerDaoImpl trainerDao;
    @Mock
    private TraineeDaoImpl traineeDao;

    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        trainingService = new TrainingService();
        ReflectionTestUtils.setField(trainingService, "trainingDao", trainingDao);
        ReflectionTestUtils.setField(trainingService, "trainerDao", trainerDao);
        ReflectionTestUtils.setField(trainingService, "traineeDao", traineeDao);
    }

    @Test
    void create_shouldPersistWhenTrainerAndTraineeExist() {
        Date date = new Date();
        TrainingDtoIn dto = new TrainingDtoIn(1L, 2L, "Morning", TrainingType.CARDIO, date, Duration.ofMinutes(45));

        when(trainerDao.selectById(1L)).thenReturn(Optional.of(new Trainer()));
        when(traineeDao.selectById(2L)).thenReturn(Optional.of(new Trainee()));
        when(trainingDao.create(any(Training.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Training result = trainingService.create(dto);

        assertEquals(1L, result.getTrainerId());
        assertEquals(2L, result.getTraineeId());
        assertEquals("Morning", result.getTrainingName());
        assertEquals(TrainingType.CARDIO, result.getTrainingType());
        assertEquals(Duration.ofMinutes(45), result.getTrainingDuration());
        assertEquals(date, result.getTrainingDate());
    }

    @Test
    void create_shouldThrowWhenTraineeMissing() {
        TrainingDtoIn dto = new TrainingDtoIn(1L, 2L, "Morning", TrainingType.CARDIO, new Date(), Duration.ofMinutes(30));

        when(traineeDao.selectById(2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> trainingService.create(dto));
        assertTrue(ex.getMessage().contains("Trainee not found"));
    }

    @Test
    void create_shouldThrowWhenTrainerMissing() {
        TrainingDtoIn dto = new TrainingDtoIn(1L, 2L, "Morning", TrainingType.CARDIO, new Date(), Duration.ofMinutes(30));

        when(traineeDao.selectById(2L)).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.selectById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> trainingService.create(dto));
        assertTrue(ex.getMessage().contains("Trainer not found"));
    }

    @Test
    void select_shouldReturnTrainingWhenExists() {
        Training training = new Training();
        when(trainingDao.getById(7L)).thenReturn(Optional.of(training));

        Training result = trainingService.select(7L);

        assertSame(training, result);
    }

    @Test
    void select_shouldThrowWhenMissing() {
        when(trainingDao.getById(7L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> trainingService.select(7L));

        assertTrue(ex.getMessage().contains("7"));
    }
}

