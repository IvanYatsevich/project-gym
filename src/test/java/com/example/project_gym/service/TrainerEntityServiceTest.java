package com.example.project_gym.service;

import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.domain.entity.TrainingType;
import com.example.project_gym.domain.entity.User;
import com.example.project_gym.model.request.CreateTrainerRequest;
import com.example.project_gym.model.request.PasswordChangeRequest;
import com.example.project_gym.model.request.TrainerTrainingsFilterRequest;
import com.example.project_gym.model.request.UpdateTrainerRequest;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.repository.idao.TrainingTypeDAO;
import com.example.project_gym.security.AuthenticationGuard;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerEntityServiceTest {

    @Mock
    private TrainerDAO trainerDao;
    @Mock
    private TrainingTypeDAO trainingTypeDao;
    @Mock
    private PasswordChangeService passwordChangeService;
    @Mock
    private UniqueUserNameGenerator userNameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private AuthenticationGuard authGuard;

    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerService();
        ReflectionTestUtils.setField(trainerService, "trainerDao", trainerDao);
        ReflectionTestUtils.setField(trainerService, "trainingTypeDao", trainingTypeDao);
        ReflectionTestUtils.setField(trainerService, "passwordChangeService", passwordChangeService);
        trainerService.setUserNameGenerator(userNameGenerator);
        trainerService.setAuthenticationGuard(authGuard);
    }

    @Test
    void create_shouldCreateTrainer() {
        TrainingType cardio = new TrainingType();
        cardio.setTrainingTypeName("CARDIO");
        when(trainingTypeDao.findByTrainingTypeName("CARDIO")).thenReturn(Optional.of(cardio));
        when(userNameGenerator.generateUnique("Ivan", "Ivanov")).thenReturn("Ivan.Ivanov");

        TrainerEntity result = trainerService.create(new CreateTrainerRequest("Ivan", "Ivanov", "CARDIO"));

        assertEquals("Ivan", result.getUser().getFirstName());
        assertEquals("Ivan.Ivanov", result.getUser().getUserName());
        assertTrue(result.getUser().isActive());
        assertEquals("CARDIO", result.getTrainingType().getTrainingTypeName());
        assertNotNull(result.getUser().getPassword());
        assertEquals(10, result.getUser().getPassword().length());
        verify(trainerDao).create(any(TrainerEntity.class));
        verifyNoInteractions(authGuard);
    }

    @Test
    void selectByUsername_shouldThrowWhenMissing() {
        when(trainerDao.findByUsername("missing")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> trainerService.selectByUsername("missing"));
    }


    @Test
    void update_shouldChangeFields() {
        TrainerEntity trainerEntity = new TrainerEntity();
        User user = new User();
        user.setActive(false);
        trainerEntity.setUser(user);

        TrainingType strength = new TrainingType();
        strength.setTrainingTypeName("STRENGTH");

        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainerEntity));
        when(trainingTypeDao.findByTrainingTypeName("STRENGTH")).thenReturn(Optional.of(strength));

        TrainerEntity updated = trainerService.update(1L, new UpdateTrainerRequest(true, "STRENGTH"));

        assertTrue(updated.getUser().isActive());
        assertEquals("STRENGTH", updated.getTrainingType().getTrainingTypeName());
        verify(trainerDao).update(trainerEntity);
    }

    @Test
    void update_shouldThrowWhenDtoMissing() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.update(1L, null));
    }

    @Test
    void toggleActive_shouldFlipStatus() {
        TrainerEntity trainerEntity = new TrainerEntity();
        User user = new User();
        user.setActive(false);
        trainerEntity.setUser(user);
        when(trainerDao.findByUsername("ivan")).thenReturn(Optional.of(trainerEntity));

        TrainerEntity result = trainerService.toggleActive("ivan");

        assertTrue(result.getUser().isActive());
        verify(trainerDao).update(trainerEntity);
    }

    @Test
    void changePassword_shouldDelegate() {
        PasswordChangeRequest dto = new PasswordChangeRequest("ivan", "old", "new");

        trainerService.changePassword(dto);

        verify(passwordChangeService).changeTrainerPassword(dto);
    }

    @Test
    void getTrainings_shouldDelegateToDao() {
        List<TrainingEntity> trainingEntities = List.of(new TrainingEntity());
        LocalDateTime now = LocalDateTime.now();
        when(trainerDao.getTrainings("ivan", now, now, "petr")).thenReturn(trainingEntities);

        List<TrainingEntity> result = trainerService.getTrainings(new TrainerTrainingsFilterRequest("ivan", now, now, "petr"));

        assertEquals(1, result.size());
    }

    @Test
    void guardedOperations_shouldThrowWhenUnauthenticated() {
        doThrow(new SecurityException("Authentication required")).when(authGuard).requireAuthenticated();

        assertAll(
                () -> assertThrows(SecurityException.class, () -> trainerService.selectByUsername("ivan")),
                () -> assertThrows(SecurityException.class, () -> trainerService.changePassword(new PasswordChangeRequest("ivan", "old", "new"))),
                () -> assertThrows(SecurityException.class, () -> trainerService.update(1L, new UpdateTrainerRequest(true, "CARDIO"))),
                () -> assertThrows(SecurityException.class, () -> trainerService.toggleActive("ivan")),
                () -> assertThrows(SecurityException.class, () -> trainerService.getTrainings(new TrainerTrainingsFilterRequest("ivan", null, null, null))),
                () -> assertThrows(SecurityException.class, () -> trainerService.select(1L))
        );
    }
}
