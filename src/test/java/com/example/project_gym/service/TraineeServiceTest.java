package com.example.project_gym.service;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import com.example.project_gym.model.User;
import com.example.project_gym.model.dto.dtoin.TraineeDtoIn;
import com.example.project_gym.model.dto.dtoin.TrainingFilterDto;
import com.example.project_gym.model.dto.dtoupdate.TraineeUpdateDto;
import com.example.project_gym.repository.idao.ITraineeDAO;
import com.example.project_gym.utilservices.authservices.PasswordChangeService;
import com.example.project_gym.utilservices.unauthservices.password.PasswordGenerator;
import com.example.project_gym.utilservices.unauthservices.username.UniqueUserNameGenerator;
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
class TraineeServiceTest {

    @Mock
    private ITraineeDAO traineeDao;
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

        Trainee trainee = traineeService.create(new TraineeDtoIn("Hulk", "Hogan", dob, "NY"));

        assertEquals("Hulk", trainee.getUser().getFirstName());
        assertEquals("Hulk.Hogan", trainee.getUser().getUserName());
        assertEquals("NY", trainee.getAddress());
        assertEquals(dob, trainee.getDateOfBirth());
        verify(traineeDao).create(any(Trainee.class));
    }

    @Test
    void create_shouldThrowWhenFirstNameBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.create(new TraineeDtoIn(" ", "Hogan", null, null)));
    }

    @Test
    void selectByUsername_shouldThrowWhenMissing() {
        when(traineeDao.selectByUsername("missing")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> traineeService.selectByUsername("missing"));
    }

    @Test
    void update_shouldChangeAddressAndStatus() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setActive(false);
        trainee.setUser(user);
        when(traineeDao.selectById(1L)).thenReturn(Optional.of(trainee));

        Trainee updated = traineeService.update(1L, new TraineeUpdateDto(true, "LA"));

        assertTrue(updated.getUser().isActive());
        assertEquals("LA", updated.getAddress());
        verify(traineeDao).update(trainee);
    }

    @Test
    void update_shouldThrowWhenDtoMissing() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.update(1L, null));
    }

    @Test
    void getTrainings_shouldDelegateToDao() {
        Date now = new Date();
        List<Training> trainings = List.of(new Training());
        when(traineeDao.getTrainings("hulk", now, now, "ivan", "CARDIO")).thenReturn(trainings);

        List<Training> result = traineeService.getTrainings(new TrainingFilterDto("hulk", now, now, "ivan", "CARDIO"));

        assertEquals(1, result.size());
    }

    @Test
    void getUnassignedTrainers_shouldDelegateToDao() {
        when(traineeDao.findUnassignedTrainers("hulk")).thenReturn(List.of(new Trainer()));

        List<Trainer> result = traineeService.getUnassignedTrainers("hulk");

        assertEquals(1, result.size());
    }

    @Test
    void authenticate_shouldReturnTrueForCorrectPassword() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setPassword("secret");
        trainee.setUser(user);
        when(traineeDao.selectByUsername("hulk")).thenReturn(Optional.of(trainee));

        assertTrue(traineeService.authenticate("hulk", "secret"));
    }

    @Test
    void authenticate_shouldThrowWhenMissing() {
        when(traineeDao.selectByUsername("none")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> traineeService.authenticate("none", "123"));
    }

    @Test
    void toggleActive_shouldFlipStatus() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setActive(true);
        trainee.setUser(user);
        when(traineeDao.selectByUsername("hulk")).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.toggleActive("hulk");

        assertFalse(result.getUser().isActive());
        verify(traineeDao).update(trainee);
    }

    @Test
    void updateTrainersList_shouldDelegate() {
        Trainee trainee = new Trainee();
        trainee.setUser(new User());
        when(traineeDao.selectByUsername("hulk")).thenReturn(Optional.of(trainee));
        List<Trainer> trainers = List.of(new Trainer());

        traineeService.updateTrainersList("hulk", trainers);

        verify(traineeDao).updateTrainersList(trainee, trainers);
    }

    @Test
    void deleteByUsername_shouldDelegate() {
        when(traineeDao.deleteByUsername("hulk")).thenReturn(true);

        assertTrue(traineeService.deleteByUsername("hulk"));
    }
}
