package com.example.project_gym.service;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.User;
import com.example.project_gym.domain.entity.UserType;
import com.example.project_gym.model.request.LoginRequest;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.security.AuthorizationContext;
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
class AuthenticationServiceTest {

    @Mock
    private TraineeDAO traineeDao;
    @Mock
    private TrainerDAO trainerDao;
    @Mock
    private AuthenticationService service;
    @Mock
    private AuthorizationContext authorizationContext;

    @BeforeEach
    void setUp() {
        service = new AuthenticationService();
        ReflectionTestUtils.setField(service, "traineeDAO", traineeDao);
        ReflectionTestUtils.setField(service, "trainerDAO", trainerDao);
        authorizationContext = new AuthorizationContext();
        service.setAuthorizationContext(authorizationContext);
    }

    @Test
    void authenticate_shouldReturnTrainee() throws AuthenticationException {
        TraineeEntity traineeEntity = new TraineeEntity();
        User user = new User();
        user.setPassword("123");
        traineeEntity.setUser(user);
        when(traineeDao.findByUsername("john")).thenReturn(Optional.of(traineeEntity));

        UserType result = service.authenticate("john", "123");

        assertEquals(UserType.TRAINEE, result);
        assertTrue(authorizationContext.isAuthenticated());
        assertEquals("john", authorizationContext.getUsername());
        assertEquals(UserType.TRAINEE, authorizationContext.getUserType());
    }

    @Test
    void authenticate_shouldReturnTrainer() throws AuthenticationException {
        TrainerEntity trainerEntity = new TrainerEntity();
        User user = new User();
        user.setPassword("123");
        trainerEntity.setUser(user);
        when(traineeDao.findByUsername("kate")).thenReturn(Optional.empty());
        when(trainerDao.findByUsername("kate")).thenReturn(Optional.of(trainerEntity));

        UserType result = service.authenticate(new LoginRequest("kate", "123"));

        assertEquals(UserType.TRAINER, result);
        assertTrue(authorizationContext.isAuthenticated());
        assertEquals("kate", authorizationContext.getUsername());
        assertEquals(UserType.TRAINER, authorizationContext.getUserType());
    }

    @Test
    void authenticate_shouldThrowWhenInvalid() {
        when(traineeDao.findByUsername("bad")).thenReturn(Optional.empty());
        when(trainerDao.findByUsername("bad")).thenReturn(Optional.empty());

        authorizationContext.authenticate("old-user", UserType.TRAINEE);

        assertThrows(AuthenticationException.class, () -> service.authenticate("bad", "111"));
        assertFalse(authorizationContext.isAuthenticated());
        assertNull(authorizationContext.getUsername());
        assertEquals(UserType.UNKNOWN, authorizationContext.getUserType());
    }

    @Test
    void logout_shouldClearAuthorizationContext() {
        authorizationContext.authenticate("john", UserType.TRAINEE);

        service.logout();

        assertFalse(authorizationContext.isAuthenticated());
        assertNull(authorizationContext.getUsername());
        assertEquals(UserType.UNKNOWN, authorizationContext.getUserType());
    }
}

