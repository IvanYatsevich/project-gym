package com.example.project_gym.service;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import com.example.project_gym.model.TrainingType;
import com.example.project_gym.model.dto.dtoin.TrainingDtoIn;
import com.example.project_gym.repository.idao.ITraineeDAO;
import com.example.project_gym.repository.idao.ITrainerDAO;
import com.example.project_gym.repository.idao.ITrainingDAO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private ITrainingDAO trainingDao;
    @Mock
    private ITrainerDAO trainerDao;
    @Mock
    private ITraineeDAO traineeDao;

    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        trainingService = new TrainingService();
        ReflectionTestUtils.setField(trainingService, "trainingDao", trainingDao);
        ReflectionTestUtils.setField(trainingService, "trainerDao", trainerDao);
        ReflectionTestUtils.setField(trainingService, "traineeDao", traineeDao);
    }

    @Test
    void create_shouldCreateTraining() {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("CARDIO");
        Trainer trainer = new Trainer();
        Trainee trainee = new Trainee();
        when(trainerDao.selectById(1L)).thenReturn(Optional.of(trainer));
        when(traineeDao.selectById(2L)).thenReturn(Optional.of(trainee));
        when(trainingDao.create(any(Training.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Training result = trainingService.create(new TrainingDtoIn(1L, 2L, "Morning", type, new Date(), 45L));

        assertSame(trainer, result.getTrainer());
        assertSame(trainee, result.getTrainee());
        assertEquals("Morning", result.getTrainingName());
    }

    @Test
    void create_shouldThrowWhenTraineeMissing() {
        TrainingType type = new TrainingType();
        when(traineeDao.selectById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> trainingService.create(new TrainingDtoIn(1L, 2L, "Morning", type, new Date(), 45L)));
    }

    @Test
    void create_shouldThrowWhenTrainerMissing() {
        TrainingType type = new TrainingType();
        when(traineeDao.selectById(2L)).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.selectById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> trainingService.create(new TrainingDtoIn(1L, 2L, "Morning", type, new Date(), 45L)));
    }

    @Test
    void create_shouldThrowWhenNameBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.create(new TrainingDtoIn(1L, 2L, " ", new TrainingType(), new Date(), 45L)));
    }

    @Test
    void create_shouldThrowWhenDurationInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.create(new TrainingDtoIn(1L, 2L, "Morning", new TrainingType(), new Date(), 0L)));
    }

    @Test
    void select_shouldThrowWhenMissing() {
        when(trainingDao.getById(10L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> trainingService.select(10L));
    }

    @Test
    void getAll_shouldDelegateToDao() {
        when(trainingDao.getAll()).thenReturn(List.of(new Training(), new Training()));
        assertEquals(2, trainingService.getAll().size());
    }
}
