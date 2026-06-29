package com.example.project_gym.service;
import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.User;
import com.example.project_gym.exception.TraineeNotFoundException;
import com.example.project_gym.mapper.TraineeMapper;
import com.example.project_gym.mapper.TrainerMapper;
import com.example.project_gym.mapper.TrainingMapper;
import com.example.project_gym.model.request.TraineeTrainingsRequest;
import com.example.project_gym.model.request.create.TraineeCreateRequest;
import com.example.project_gym.model.request.get.TraineeRequest;
import com.example.project_gym.model.request.update.TraineeTrainersUpdateRequest;
import com.example.project_gym.model.request.update.TraineeUpdateRequest;
import com.example.project_gym.model.request.update.UserActivationRequest;
import com.example.project_gym.model.response.create.TraineeCreateResponse;
import com.example.project_gym.model.response.get.TraineeResponse;
import com.example.project_gym.model.response.get.TraineeTrainerResponse;
import com.example.project_gym.model.response.update.TraineeTrainersUpdateResponse;
import com.example.project_gym.model.response.update.TraineeUpdateResponse;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.security.AuthenticationGuard;
import com.example.project_gym.utilservices.authenticatedservices.PasswordChangeService;
import com.example.project_gym.utilservices.guestservices.password.PasswordGenerator;
import com.example.project_gym.utilservices.guestservices.username.UniqueUserNameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {
    @Mock
    private TraineeDAO traineeDao;
    @Mock
    private PasswordChangeService passwordChangeService;
    @Mock
    private UniqueUserNameGenerator nameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private AuthenticationGuard authGuard;
    @Mock
    private TraineeMapper traineeMapper;
    @Mock
    private TrainingMapper trainingMapper;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private TrainerDAO trainerDao;
    private TraineeService service;
    @BeforeEach
    void setUp() {
        service = new TraineeService(traineeDao, passwordChangeService, nameGenerator, authGuard, traineeMapper, trainingMapper, trainerMapper, trainerDao, passwordGenerator);
    }
    @Test
    void service_shouldBeInitialized() {
        assertNotNull(service);
    }

    @Test
    void create_shouldCreateTraineeSuccessfully() {
        TraineeCreateRequest request = new TraineeCreateRequest("John", "Doe", null, "USA");
        TraineeEntity entity = new TraineeEntity();
        User user = new User();
        entity.setUser(user);
        TraineeCreateResponse response = new TraineeCreateResponse("john.doe", "password123");

        when(traineeMapper.toEntity(request)).thenReturn(entity);
        when(nameGenerator.generateUnique("John", "Doe")).thenReturn("john.doe");
        when(passwordGenerator.generatePassword()).thenReturn("password123");
        when(traineeDao.create(any(TraineeEntity.class))).thenReturn(entity);
        when(traineeMapper.toCreateResponse(entity)).thenReturn(response);

        TraineeCreateResponse result = service.create(request);

        assertEquals("john.doe", result.username());
        assertNotNull(result);
    }

    @Test
    void selectByUsername_shouldReturnTraineeWhenFound() {
        TraineeRequest request = new TraineeRequest("john.doe");
        TraineeEntity entity = new TraineeEntity();
        TraineeResponse response = new TraineeResponse("John", "Doe", null, "USA", true, List.of());

        when(traineeDao.getByUsername("john.doe")).thenReturn(Optional.of(entity));
        when(traineeMapper.toResponse(entity)).thenReturn(response);

        TraineeResponse result = service.selectByUsername(request);

        assertNotNull(result);
        assertEquals("John", result.firstName());
    }

    @Test
    void selectByUsername_shouldThrowExceptionWhenNotFound() {
        TraineeRequest request = new TraineeRequest("nonexistent");

        when(traineeDao.getByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () -> service.selectByUsername(request));
    }

    @Test
    void update_shouldUpdateTraineeSuccessfully() {
        TraineeUpdateRequest request = new TraineeUpdateRequest("john.doe", "Jane", "Smith", null, "Canada", true);
        TraineeEntity entity = new TraineeEntity();
        User user = new User();
        entity.setUser(user);
        TraineeUpdateResponse response = new TraineeUpdateResponse("john.doe", "Jane", "Smith", null, "Canada", true, List.of());

        when(traineeDao.getByUsername("john.doe")).thenReturn(Optional.of(entity));
        when(traineeDao.update(any(TraineeEntity.class))).thenReturn(entity);
        when(traineeMapper.toUpdateResponse(entity)).thenReturn(response);

        TraineeUpdateResponse result = service.update(request);

        assertNotNull(result);
        assertEquals("john.doe", result.username());
    }

    @Test
    void activateDeactivate_shouldUpdateActiveStatus() {
        UserActivationRequest request = new UserActivationRequest("john.doe", false);
        TraineeEntity entity = new TraineeEntity();
        User user = new User();
        user.setActive(true);
        entity.setUser(user);

        when(traineeDao.getByUsername("john.doe")).thenReturn(Optional.of(entity));

        service.activateDeactivate(request);

        org.mockito.Mockito.verify(traineeDao).update(any(TraineeEntity.class));
    }

    @Test
    void deleteByUsername_shouldDeleteTrainee() {
        TraineeRequest request = new TraineeRequest("john.doe");

        when(traineeDao.deleteByUsername("john.doe")).thenReturn(true);

        boolean result = service.deleteByUsername(request);

        assertTrue(result);
    }

    @Test
    void getTrainings_shouldReturnTraineeTrainings() {
        TraineeTrainingsRequest request = new TraineeTrainingsRequest("john.doe", null, null, null, null);

        when(traineeDao.getTrainings("john.doe", null, null, null, null)).thenReturn(List.of());

        var result = service.getTrainings(request);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void getUnassignedTrainers_shouldReturnUnassignedTrainers() {
        TraineeRequest request = new TraineeRequest("john.doe");

        when(traineeDao.getUnassignedTrainers("john.doe")).thenReturn(List.of());

        var result = service.getUnassignedTrainers(request);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}