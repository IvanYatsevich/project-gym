package com.example.project_gym.service;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingTypeEntity;
import com.example.project_gym.domain.entity.User;
import com.example.project_gym.exception.TrainerNotFoundException;
import com.example.project_gym.mapper.TrainerMapper;
import com.example.project_gym.mapper.TrainingMapper;
import com.example.project_gym.model.request.TrainerTrainingsRequest;
import com.example.project_gym.model.request.create.TrainerCreateRequest;
import com.example.project_gym.model.request.get.TrainerRequest;
import com.example.project_gym.model.request.update.TrainerUpdateRequest;
import com.example.project_gym.model.request.update.UserActivationRequest;
import com.example.project_gym.model.response.create.TrainerCreateResponse;
import com.example.project_gym.model.response.get.TrainerResponse;
import com.example.project_gym.model.response.update.TrainerUpdateResponse;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.repository.idao.TrainingTypeDAO;
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
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {
    @Mock
    private TrainerDAO trainerDao;
    @Mock
    private TrainingTypeDAO trainingTypeDao;
    @Mock
    private PasswordChangeService passwordChangeService;
    @Mock
    private UniqueUserNameGenerator nameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private AuthenticationGuard authGuard;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private TrainingMapper trainingMapper;
    private TrainerService service;
    @BeforeEach
    void setUp() {
        service = new TrainerService(trainerDao, trainingTypeDao, passwordChangeService, nameGenerator, passwordGenerator, authGuard, trainerMapper, trainingMapper);
    }
    @Test
    void service_shouldBeInitialized() {
        assertNotNull(service);
    }

    @Test
    void create_shouldCreateTrainerSuccessfully() {
        TrainerCreateRequest request = new TrainerCreateRequest("John", "Doe", "CARDIO");
        TrainerEntity entity = new TrainerEntity();
        User user = new User();
        entity.setUser(user);
        TrainingTypeEntity trainingType = new TrainingTypeEntity();
        trainingType.setTrainingTypeName("CARDIO");
        entity.setTrainingTypeEntity(trainingType);
        TrainerCreateResponse response = new TrainerCreateResponse("john.doe", "password123");

        when(trainingTypeDao.findByTrainingTypeName("CARDIO")).thenReturn(Optional.of(trainingType));
        when(trainerMapper.toEntity(request)).thenReturn(entity);
        when(nameGenerator.generateUnique("John", "Doe")).thenReturn("john.doe");
        when(passwordGenerator.generatePassword()).thenReturn("password123");
        when(trainerDao.create(any(TrainerEntity.class))).thenReturn(entity);
        when(trainerMapper.toCreateResponse(entity)).thenReturn(response);

        TrainerCreateResponse result = service.create(request);

        assertNotNull(result);
        assertEquals("john.doe", result.username());
    }

    @Test
    void selectByUsername_shouldReturnTrainerWhenFound() {
        TrainerRequest request = new TrainerRequest("john.doe");
        TrainerEntity entity = new TrainerEntity();
        TrainingTypeEntity trainingType = new TrainingTypeEntity();
        entity.setTrainingTypeEntity(trainingType);
        TrainerResponse response = new TrainerResponse("John", "Doe", trainingType, true, List.of());

        when(trainerDao.getByUsername("john.doe")).thenReturn(Optional.of(entity));
        when(trainerMapper.toGetResponse(entity)).thenReturn(response);

        TrainerResponse result = service.selectByUsername(request);

        assertNotNull(result);
        assertEquals("John", result.firstName());
    }

    @Test
    void selectByUsername_shouldThrowExceptionWhenNotFound() {
        TrainerRequest request = new TrainerRequest("nonexistent");

        when(trainerDao.getByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () -> service.selectByUsername(request));
    }

    @Test
    void update_shouldUpdateTrainerSuccessfully() {
        TrainingTypeEntity trainingType = new TrainingTypeEntity();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("STRENGTH");
        TrainerUpdateRequest request = new TrainerUpdateRequest("john.doe", "Jane", "Smith", trainingType, true);
        TrainerEntity entity = new TrainerEntity();
        User user = new User();
        entity.setUser(user);
        entity.setTrainingTypeEntity(trainingType);
        TrainerUpdateResponse response = new TrainerUpdateResponse("john.doe", "Jane", "Smith", trainingType, true, List.of());

        when(trainerDao.getByUsername("john.doe")).thenReturn(Optional.of(entity));
        when(trainerDao.update(any(TrainerEntity.class))).thenReturn(entity);
        when(trainerMapper.toUpdateResponse(entity)).thenReturn(response);

        TrainerUpdateResponse result = service.update(request);

        assertNotNull(result);
        assertEquals("john.doe", result.username());
    }

    // ...existing code...

    @Test
    void toggleActive_shouldUpdateActiveStatus() {
        UserActivationRequest request = new UserActivationRequest("john.doe", false);
        TrainerEntity entity = new TrainerEntity();
        User user = new User();
        user.setActive(true);
        entity.setUser(user);

        when(trainerDao.getByUsername("john.doe")).thenReturn(Optional.of(entity));

        service.toggleActive(request);

        org.mockito.Mockito.verify(trainerDao).update(any(TrainerEntity.class));
    }

    @Test
    void getTrainings_shouldReturnTrainerTrainings() {
        TrainerTrainingsRequest request = new TrainerTrainingsRequest("john.doe", null, null, null);

        when(trainerDao.getTrainings("john.doe", null, null, null)).thenReturn(List.of());

        var result = service.getTrainings(request);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}