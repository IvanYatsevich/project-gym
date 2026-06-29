package com.example.project_gym.service;
import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.domain.entity.User;
import com.example.project_gym.exception.TraineeNotFoundException;
import com.example.project_gym.exception.TrainerNotFoundException;
import com.example.project_gym.exception.TrainingNotFoundException;
import com.example.project_gym.mapper.TrainingMapper;
import com.example.project_gym.model.request.create.TrainingCreateRequest;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.repository.idao.TrainingDAO;
import com.example.project_gym.security.AuthenticationGuard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {
    @Mock
    private TrainingDAO trainingDao;
    @Mock
    private TrainerDAO trainerDao;
    @Mock
    private TraineeDAO traineeDao;
    @Mock
    private TrainingMapper trainingMapper;
    @Mock
    private AuthenticationGuard authGuard;
    private TrainingService service;
    @BeforeEach
    void setUp() {
        service = new TrainingService(trainingDao, trainerDao, traineeDao, trainingMapper, authGuard);
    }
    @Test
    void service_shouldBeInitialized() {
        assertNotNull(service);
    }

    @Test
    void create_shouldCreateTrainingSuccessfully() {
        LocalDateTime now = LocalDateTime.now();
        TrainingCreateRequest request = new TrainingCreateRequest("trainer1", "trainee1", "Training Session", now, 60L);
        TraineeEntity trainee = new TraineeEntity();
        User traineeUser = new User();
        traineeUser.setActive(true);
        trainee.setUser(traineeUser);
        TrainerEntity trainer = new TrainerEntity();
        User trainerUser = new User();
        trainerUser.setActive(true);
        trainer.setUser(trainerUser);
        TrainingEntity trainingEntity = new TrainingEntity();

        when(traineeDao.getByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.getByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainingMapper.toEntity(request)).thenReturn(trainingEntity);

        service.create(request);

        org.mockito.Mockito.verify(trainingDao).create(any(TrainingEntity.class));
    }

    @Test
    void create_shouldThrowExceptionWhenTraineeNotFound() {
        LocalDateTime now = LocalDateTime.now();
        TrainingCreateRequest request = new TrainingCreateRequest("trainer1", "nonexistent", "Training", now, 60L);

        when(traineeDao.getByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () -> service.create(request));
    }

    @Test
    void create_shouldThrowExceptionWhenTrainerNotFound() {
        LocalDateTime now = LocalDateTime.now();
        TrainingCreateRequest request = new TrainingCreateRequest("nonexistent", "trainee1", "Training", now, 60L);
        TraineeEntity trainee = new TraineeEntity();

        when(traineeDao.getByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.getByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () -> service.create(request));
    }

    @Test
    void select_shouldReturnTrainingWhenFound() {
        TrainingEntity entity = new TrainingEntity();
        entity.setId(1L);

        when(trainingDao.findById(1L)).thenReturn(Optional.of(entity));

        TrainingEntity result = service.select(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void select_shouldThrowExceptionWhenNotFound() {
        when(trainingDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TrainingNotFoundException.class, () -> service.select(999L));
    }

    @Test
    void getAll_shouldReturnAllTrainings() {
        List<TrainingEntity> trainings = List.of(new TrainingEntity(), new TrainingEntity());

        when(trainingDao.getAll()).thenReturn(trainings);

        List<TrainingEntity> result = service.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}