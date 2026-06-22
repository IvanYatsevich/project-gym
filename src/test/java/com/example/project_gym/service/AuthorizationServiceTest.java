package com.example.project_gym.service;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.User;
import com.example.project_gym.model.UserType;
import com.example.project_gym.model.dto.dtoin.LoginResultDto;
import com.example.project_gym.repository.idao.ITraineeDAO;
import com.example.project_gym.repository.idao.ITrainerDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.naming.AuthenticationException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private ITraineeDAO traineeDao;
    @Mock
    private ITrainerDAO trainerDao;

    private AuthorizationService service;

    @BeforeEach
    void setUp() {
        service = new AuthorizationService();
        ReflectionTestUtils.setField(service, "traineeDAO", traineeDao);
        ReflectionTestUtils.setField(service, "trainerDAO", trainerDao);
    }

    @Test
    void authenticate_shouldReturnTrainee() throws AuthenticationException {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setPassword("123");
        trainee.setUser(user);
        when(traineeDao.selectByUsername("john")).thenReturn(Optional.of(trainee));

        UserType result = service.authenticate("john", "123");

        assertEquals(UserType.TRAINEE, result);
    }

    @Test
    void authenticate_shouldReturnTrainer() throws AuthenticationException {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setPassword("123");
        trainer.setUser(user);
        when(traineeDao.selectByUsername("kate")).thenReturn(Optional.empty());
        when(trainerDao.selectByUsername("kate")).thenReturn(Optional.of(trainer));

        UserType result = service.authenticate(new LoginResultDto("kate", "123"));

        assertEquals(UserType.TRAINER, result);
    }

    @Test
    void authenticate_shouldThrowWhenInvalid() {
        when(traineeDao.selectByUsername("bad")).thenReturn(Optional.empty());
        when(trainerDao.selectByUsername("bad")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> service.authenticate("bad", "111"));
    }
}

