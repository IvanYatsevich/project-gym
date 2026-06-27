package com.example.project_gym.utilservices;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.User;
import com.example.project_gym.model.request.PasswordChangeRequest;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.security.AuthenticationGuard;
import com.example.project_gym.utilservices.authenticatedservices.PasswordChangeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordChangeServiceTest {

    @Mock
    private TraineeDAO traineeDao;
    @Mock
    private TrainerDAO trainerDao;
    @Mock
    private AuthenticationGuard authGuard;

    private PasswordChangeService service;

    @BeforeEach
    void setUp() {
        service = new PasswordChangeService();
        ReflectionTestUtils.setField(service, "traineeDao", traineeDao);
        ReflectionTestUtils.setField(service, "trainerDao", trainerDao);
        service.setAuthenticationGuard(authGuard);
    }

    @Test
    void changeTraineePassword_shouldUpdatePassword() {
        TraineeEntity traineeEntity = new TraineeEntity();
        User user = new User();
        user.setPassword("old");
        traineeEntity.setUser(user);
        when(traineeDao.findByUsername("hulk")).thenReturn(Optional.of(traineeEntity));

        service.changeTraineePassword(new PasswordChangeRequest("hulk", "old", "new"));

        assertEquals("new", traineeEntity.getUser().getPassword());
        verify(traineeDao).update(traineeEntity);
    }

    @Test
    void changeTrainerPassword_shouldUpdatePassword() {
        TrainerEntity trainerEntity = new TrainerEntity();
        User user = new User();
        user.setPassword("old");
        trainerEntity.setUser(user);
        when(trainerDao.findByUsername("ivan")).thenReturn(Optional.of(trainerEntity));

        service.changeTrainerPassword(new PasswordChangeRequest("ivan", "old", "new"));

        assertEquals("new", trainerEntity.getUser().getPassword());
        verify(trainerDao).update(trainerEntity);
    }

    @Test
    void changeTrainerPassword_shouldThrowWhenMissing() {
        when(trainerDao.findByUsername("ivan")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.changeTrainerPassword(new PasswordChangeRequest("ivan", "old", "new")));
    }

    @Test
    void changeTraineePassword_shouldThrowWhenMissing() {
        when(traineeDao.findByUsername("hulk")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.changeTraineePassword(new PasswordChangeRequest("hulk", "old", "new")));
    }

    @Test
    void changeTraineePassword_shouldThrowForBadOldPassword() {
        TraineeEntity traineeEntity = new TraineeEntity();
        User user = new User();
        user.setPassword("old");
        traineeEntity.setUser(user);
        when(traineeDao.findByUsername("hulk")).thenReturn(Optional.of(traineeEntity));

        assertThrows(IllegalArgumentException.class,
                () -> service.changeTraineePassword(new PasswordChangeRequest("hulk", "wrong", "new")));
    }

    @Test
    void changeTrainerPassword_shouldThrowForBadOldPassword() {
        TrainerEntity trainerEntity = new TrainerEntity();
        User user = new User();
        user.setPassword("old");
        trainerEntity.setUser(user);
        when(trainerDao.findByUsername("ivan")).thenReturn(Optional.of(trainerEntity));

        assertThrows(IllegalArgumentException.class,
                () -> service.changeTrainerPassword(new PasswordChangeRequest("ivan", "wrong", "new")));
    }

    @Test
    void allOperations_shouldThrowWhenUnauthenticated() {
        doThrow(new SecurityException("Authentication required")).when(authGuard).requireAuthenticated();

        assertAll(
                () -> assertThrows(SecurityException.class,
                        () -> service.changeTraineePassword(new PasswordChangeRequest("hulk", "old", "new"))),
                () -> assertThrows(SecurityException.class,
                        () -> service.changeTrainerPassword(new PasswordChangeRequest("ivan", "old", "new")))
        );
    }
}
