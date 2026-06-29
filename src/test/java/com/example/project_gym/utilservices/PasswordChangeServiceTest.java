package com.example.project_gym.utilservices;
import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.User;
import com.example.project_gym.exception.UserNotFoundException;
import com.example.project_gym.model.request.update.PasswordChangeRequest;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.security.AuthenticationGuard;
import com.example.project_gym.utilservices.authenticatedservices.PasswordChangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        service = new PasswordChangeService(traineeDao, trainerDao, authGuard);
    }
    @Test
    void changeTraineePassword_shouldUpdatePassword() {
        var traineeEntity = new TraineeEntity();
        var user = new User();
        user.setPassword("oldPass");
        traineeEntity.setUser(user);
        when(traineeDao.getByUsername("trainee1")).thenReturn(Optional.of(traineeEntity));
        var request = new PasswordChangeRequest("trainee1", "oldPass", "newPass");
        service.changeTraineePassword(request);
        assertEquals("newPass", traineeEntity.getUser().getPassword());
        verify(traineeDao).update(traineeEntity);
    }
    @Test
    void changeTrainerPassword_shouldUpdatePassword() {
        var trainerEntity = new TrainerEntity();
        var user = new User();
        user.setPassword("oldPass");
        trainerEntity.setUser(user);
        when(trainerDao.getByUsername("trainer1")).thenReturn(Optional.of(trainerEntity));
        var request = new PasswordChangeRequest("trainer1", "oldPass", "newPass");
        service.changeTrainerPassword(request);
        assertEquals("newPass", trainerEntity.getUser().getPassword());
        verify(trainerDao).update(trainerEntity);
    }

    @Test
    void changeTraineePassword_shouldThrowWhenTraineeNotFound() {
        when(traineeDao.getByUsername("unknown")).thenReturn(Optional.empty());
        var request = new PasswordChangeRequest("unknown", "oldPass", "newPass");
        assertThrows(UserNotFoundException.class, () -> service.changeTraineePassword(request));
    }

    @Test
    void changeTrainerPassword_shouldThrowWhenTrainerNotFound() {
        when(trainerDao.getByUsername("unknown")).thenReturn(Optional.empty());
        var request = new PasswordChangeRequest("unknown", "oldPass", "newPass");
        assertThrows(UserNotFoundException.class, () -> service.changeTrainerPassword(request));
    }
}