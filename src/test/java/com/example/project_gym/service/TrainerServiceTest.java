package com.example.project_gym.service;

import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import com.example.project_gym.model.TrainingType;
import com.example.project_gym.model.User;
import com.example.project_gym.model.dto.dtoin.TrainerDtoIn;
import com.example.project_gym.model.dto.dtoin.TrainerTrainingsFilterDto;
import com.example.project_gym.model.dto.dtoupdate.TrainerUpdateDto;
import com.example.project_gym.model.dto.dtoin.PasswordChangeDto;
import com.example.project_gym.repository.idao.ITrainerDAO;
import com.example.project_gym.repository.idao.ITrainingTypeDAO;
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
class TrainerServiceTest {

    @Mock
    private ITrainerDAO trainerDao;
    @Mock
    private ITrainingTypeDAO trainingTypeDao;
    @Mock
    private PasswordChangeService passwordChangeService;
    @Mock
    private UniqueUserNameGenerator userNameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;

    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerService();
        ReflectionTestUtils.setField(trainerService, "trainerDao", trainerDao);
        ReflectionTestUtils.setField(trainerService, "trainingTypeDao", trainingTypeDao);
        ReflectionTestUtils.setField(trainerService, "passwordChangeService", passwordChangeService);
        trainerService.setUserNameGenerator(userNameGenerator);
        trainerService.setPasswordGenerator(passwordGenerator);
    }

    @Test
    void create_shouldCreateTrainer() {
        TrainingType cardio = new TrainingType();
        cardio.setTrainingTypeName("CARDIO");
        when(trainingTypeDao.findByTrainingTypeName("CARDIO")).thenReturn(Optional.of(cardio));
        when(userNameGenerator.generateUnique("Ivan", "Ivanov")).thenReturn("Ivan.Ivanov");
        when(passwordGenerator.generatePassword()).thenReturn("Pass12345");

        Trainer result = trainerService.create(new TrainerDtoIn("Ivan", "Ivanov", "CARDIO"));

        assertEquals("Ivan", result.getUser().getFirstName());
        assertEquals("Ivan.Ivanov", result.getUser().getUserName());
        assertTrue(result.getUser().isActive());
        assertEquals("CARDIO", result.getTrainingType().getTrainingTypeName());
        verify(trainerDao).create(any(Trainer.class));
    }

    @Test
    void selectByUsername_shouldThrowWhenMissing() {
        when(trainerDao.selectByUsername("missing")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> trainerService.selectByUsername("missing"));
    }

    @Test
    void authenticate_shouldReturnTrueForCorrectPassword() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setPassword("secret");
        trainer.setUser(user);
        when(trainerDao.selectByUsername("ivan")).thenReturn(Optional.of(trainer));

        assertTrue(trainerService.authenticate("ivan", "secret"));
    }

    @Test
    void authenticate_shouldThrowWhenUserMissing() {
        when(trainerDao.selectByUsername("none")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> trainerService.authenticate("none", "123"));
    }

    @Test
    void update_shouldChangeFields() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setActive(false);
        trainer.setUser(user);

        TrainingType strength = new TrainingType();
        strength.setTrainingTypeName("STRENGTH");

        when(trainerDao.selectById(1L)).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByTrainingTypeName("STRENGTH")).thenReturn(Optional.of(strength));

        Trainer updated = trainerService.update(1L, new TrainerUpdateDto(true, "STRENGTH"));

        assertTrue(updated.getUser().isActive());
        assertEquals("STRENGTH", updated.getTrainingType().getTrainingTypeName());
        verify(trainerDao).update(trainer);
    }

    @Test
    void update_shouldThrowWhenDtoMissing() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.update(1L, null));
    }

    @Test
    void toggleActive_shouldFlipStatus() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setActive(false);
        trainer.setUser(user);
        when(trainerDao.selectByUsername("ivan")).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.toggleActive("ivan");

        assertTrue(result.getUser().isActive());
        verify(trainerDao).update(trainer);
    }

    @Test
    void changePassword_shouldDelegate() {
        PasswordChangeDto dto = new PasswordChangeDto("ivan", "old", "new");

        trainerService.changePassword(dto);

        verify(passwordChangeService).changeTrainerPassword(dto);
    }

    @Test
    void getTrainings_shouldDelegateToDao() {
        List<Training> trainings = List.of(new Training());
        Date now = new Date();
        when(trainerDao.getTrainings("ivan", now, now, "petr")).thenReturn(trainings);

        List<Training> result = trainerService.getTrainings(new TrainerTrainingsFilterDto("ivan", now, now, "petr"));

        assertEquals(1, result.size());
    }
}
