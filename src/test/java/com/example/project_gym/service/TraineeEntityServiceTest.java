package com.example.project_gym.service;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.domain.entity.User;
import com.example.project_gym.model.request.CreateTraineeRequest;
import com.example.project_gym.model.request.TraineeTrainingsFilterRequest;
import com.example.project_gym.model.request.UpdateTraineeRequest;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.utilservices.authenticatedservices.PasswordChangeService;
import com.example.project_gym.utilservices.guestservices.password.PasswordGenerator;
import com.example.project_gym.utilservices.guestservices.username.UniqueUserNameGenerator;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeEntityServiceTest {

    @Mock
    private TraineeDAO traineeDao;
    @Mock
    private PasswordChangeService passwordChangeService;
    @Mock
    private UniqueUserNameGenerator userNameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;

    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeService = new TraineeService();
        ReflectionTestUtils.setField(traineeService, "traineeDao", traineeDao);
        ReflectionTestUtils.setField(traineeService, "passwordChangeService", passwordChangeService);
        traineeService.setUserNameGenerator(userNameGenerator);
        traineeService.setPasswordGenerator(passwordGenerator);
    }

    @Test
    void create_shouldCreateTrainee() {
        Date dob = new Date();
        when(userNameGenerator.generateUnique("Hulk", "Hogan")).thenReturn("Hulk.Hogan");
        when(passwordGenerator.generatePassword()).thenReturn("Pass12345");

        TraineeEntity traineeEntity = traineeService.create(new CreateTraineeRequest("Hulk", "Hogan", dob, "NY"));

        assertEquals("Hulk", traineeEntity.getUser().getFirstName());
        assertEquals("Hulk.Hogan", traineeEntity.getUser().getUserName());
        assertEquals("NY", traineeEntity.getAddress());
        assertEquals(dob, traineeEntity.getDateOfBirth());
        verify(traineeDao).create(any(TraineeEntity.class));
    }

    @Test
    void create_shouldThrowWhenFirstNameBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.create(new CreateTraineeRequest(" ", "Hogan", null, null)));
    }

    @Test
    void selectByUsername_shouldThrowWhenMissing() {
        when(traineeDao.findByUsername("missing")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> traineeService.selectByUsername("missing"));
    }

    @Test
    void update_shouldChangeAddressAndStatus() {
        TraineeEntity traineeEntity = new TraineeEntity();
        User user = new User();
        user.setActive(false);
        traineeEntity.setUser(user);
        when(traineeDao.findById(1L)).thenReturn(Optional.of(traineeEntity));

        TraineeEntity updated = traineeService.update(1L, new UpdateTraineeRequest(true, "LA"));

        assertTrue(updated.getUser().isActive());
        assertEquals("LA", updated.getAddress());
        verify(traineeDao).update(traineeEntity);
    }

    @Test
    void update_shouldThrowWhenDtoMissing() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.update(1L, null));
    }

    @Test
    void getTrainings_shouldDelegateToDao() {
        Date now = new Date();
        List<TrainingEntity> trainingEntities = List.of(new TrainingEntity());
        when(traineeDao.getTrainings("hulk", now, now, "ivan", "CARDIO")).thenReturn(trainingEntities);

        List<TrainingEntity> result = traineeService.getTrainings(new TraineeTrainingsFilterRequest("hulk", now, now, "ivan", "CARDIO"));

        assertEquals(1, result.size());
    }

    @Test
    void getUnassignedTrainers_shouldDelegateToDao() {
        when(traineeDao.findUnassignedTrainers("hulk")).thenReturn(List.of(new TrainerEntity()));

        List<TrainerEntity> result = traineeService.getUnassignedTrainers("hulk");

        assertEquals(1, result.size());
    }

    @Test
    void toggleActive_shouldFlipStatus() {
        TraineeEntity traineeEntity = new TraineeEntity();
        User user = new User();
        user.setActive(true);
        traineeEntity.setUser(user);
        when(traineeDao.findByUsername("hulk")).thenReturn(Optional.of(traineeEntity));

        TraineeEntity result = traineeService.toggleActive("hulk");

        assertFalse(result.getUser().isActive());
        verify(traineeDao).update(traineeEntity);
    }

    @Test
    void updateTrainersList_shouldDelegate() {
        TraineeEntity traineeEntity = new TraineeEntity();
        traineeEntity.setUser(new User());
        when(traineeDao.findByUsername("hulk")).thenReturn(Optional.of(traineeEntity));
        List<TrainerEntity> trainerEntities = List.of(new TrainerEntity());

        traineeService.updateTrainersList("hulk", trainerEntities);

        verify(traineeDao).updateTrainersList(traineeEntity, trainerEntities);
    }

    @Test
    void deleteByUsername_shouldDelegate() {
        when(traineeDao.deleteByUsername("hulk")).thenReturn(true);

        assertTrue(traineeService.deleteByUsername("hulk"));
    }
}
