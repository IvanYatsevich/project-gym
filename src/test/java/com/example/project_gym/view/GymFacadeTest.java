package com.example.project_gym.view;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import com.example.project_gym.model.TrainingType;
import com.example.project_gym.service.TraineeService;
import com.example.project_gym.service.TrainerService;
import com.example.project_gym.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TrainerService trainerService;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainingService trainingService;

    private GymFacade facade;

    @BeforeEach
    void setUp() {
        facade = new GymFacade(trainerService, traineeService, trainingService);
    }

    @Test
    void shouldDelegateTrainerOperations() {
        Trainer trainer = new Trainer();
        when(trainerService.create(any())).thenReturn(trainer);
        when(trainerService.update(any(), any())).thenReturn(trainer);
        when(trainerService.select(1L)).thenReturn(trainer);

        assertSame(trainer, facade.createTrainer("A", "B", TrainingType.CARDIO));
        assertSame(trainer, facade.updateTrainer(1L, true, TrainingType.STRENGTH));
        assertSame(trainer, facade.getTrainer(1L));

        verify(trainerService).create(any());
        verify(trainerService).update(any(), any());
        verify(trainerService).select(1L);
    }

    @Test
    void shouldDelegateTraineeOperations() {
        Trainee trainee = new Trainee();
        when(traineeService.create(any())).thenReturn(trainee);
        when(traineeService.update(any(), any())).thenReturn(trainee);
        when(traineeService.select(2L)).thenReturn(trainee);
        when(traineeService.delete(2L)).thenReturn(true);

        assertSame(trainee, facade.createTrainee("A", "B", new Date(), "Addr"));
        assertSame(trainee, facade.updateTrainee(2L, false, "New"));
        assertSame(trainee, facade.getTrainee(2L));
        facade.deleteTrainee(2L);

        verify(traineeService).create(any());
        verify(traineeService).update(any(), any());
        verify(traineeService).select(2L);
        verify(traineeService).delete(2L);
    }

    @Test
    void shouldDelegateTrainingOperations() {
        Training training = new Training();
        when(trainingService.create(any())).thenReturn(training);
        when(trainingService.select(3L)).thenReturn(training);

        assertSame(training, facade.createTraining(1L, 2L, "Morning", TrainingType.CARDIO, new Date(), Duration.ofMinutes(30)));
        assertSame(training, facade.getTraining(3L));

        verify(trainingService).create(any());
        verify(trainingService).select(3L);
    }
}

