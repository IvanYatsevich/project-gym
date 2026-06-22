package com.example.project_gym.utilservices;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.User;
import com.example.project_gym.model.dto.dtoin.PasswordChangeDto;
import com.example.project_gym.repository.idao.ITraineeDAO;
import com.example.project_gym.repository.idao.ITrainerDAO;
import com.example.project_gym.utilservices.authservices.PasswordChangeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordChangeServiceTest {

    @Mock
    private ITraineeDAO traineeDao;
    @Mock
    private ITrainerDAO trainerDao;

    private PasswordChangeService service;

    @BeforeEach
    void setUp() {
        service = new PasswordChangeService();
        ReflectionTestUtils.setField(service, "traineeDao", traineeDao);
        ReflectionTestUtils.setField(service, "trainerDao", trainerDao);
    }

    @Test
    void changeTraineePassword_shouldUpdatePassword() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setPassword("old");
        trainee.setUser(user);
        when(traineeDao.selectByUsername("hulk")).thenReturn(Optional.of(trainee));

        service.changeTraineePassword(new PasswordChangeDto("hulk", "old", "new"));

        assertEquals("new", trainee.getUser().getPassword());
        verify(traineeDao).update(trainee);
    }

    @Test
    void changeTrainerPassword_shouldUpdatePassword() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setPassword("old");
        trainer.setUser(user);
        when(trainerDao.selectByUsername("ivan")).thenReturn(Optional.of(trainer));

        service.changeTrainerPassword(new PasswordChangeDto("ivan", "old", "new"));

        assertEquals("new", trainer.getUser().getPassword());
        verify(trainerDao).update(trainer);
    }

    @Test
    void changeTrainerPassword_shouldThrowWhenMissing() {
        when(trainerDao.selectByUsername("ivan")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.changeTrainerPassword(new PasswordChangeDto("ivan", "old", "new")));
    }

    @Test
    void changeTraineePassword_shouldThrowWhenMissing() {
        when(traineeDao.selectByUsername("hulk")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.changeTraineePassword(new PasswordChangeDto("hulk", "old", "new")));
    }

    @Test
    void changeTraineePassword_shouldThrowForBadOldPassword() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setPassword("old");
        trainee.setUser(user);
        when(traineeDao.selectByUsername("hulk")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalArgumentException.class,
                () -> service.changeTraineePassword(new PasswordChangeDto("hulk", "wrong", "new")));
    }

    @Test
    void changeTrainerPassword_shouldThrowForBadOldPassword() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setPassword("old");
        trainer.setUser(user);
        when(trainerDao.selectByUsername("ivan")).thenReturn(Optional.of(trainer));

        assertThrows(IllegalArgumentException.class,
                () -> service.changeTrainerPassword(new PasswordChangeDto("ivan", "wrong", "new")));
    }
}
