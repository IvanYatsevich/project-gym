package com.example.project_gym.utilservices;
import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.repository.idao.TraineeDAO;
import com.example.project_gym.repository.idao.TrainerDAO;
import com.example.project_gym.utilservices.guestservices.username.UsernameLookupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class UsernameLookupServiceTest {
    @Mock
    private TrainerDAO trainerDao;
    @Mock
    private TraineeDAO traineeDao;
    private UsernameLookupService service;
    @BeforeEach
    void setUp() {
        service = new UsernameLookupService();
        service.setTrainerDao(trainerDao);
        service.setTraineeDao(traineeDao);
    }
    @Test
    void existsByUserName_shouldReturnTrue_whenTraineeExists() {
        var traineeEntity = new TraineeEntity();
        when(traineeDao.getByUsername("trainee1")).thenReturn(Optional.of(traineeEntity));
        when(trainerDao.getByUsername("trainee1")).thenReturn(Optional.empty());
        assertTrue(service.existsByUserName("trainee1"));
    }
    @Test
    void existsByUserName_shouldReturnTrue_whenTrainerExists() {
        var trainerEntity = new TrainerEntity();
        when(trainerDao.getByUsername("trainer1")).thenReturn(Optional.of(trainerEntity));
        assertTrue(service.existsByUserName("trainer1"));
    }
    @Test
    void existsByUserName_shouldReturnFalse_whenUserDoesNotExist() {
        when(traineeDao.getByUsername("unknown")).thenReturn(Optional.empty());
        when(trainerDao.getByUsername("unknown")).thenReturn(Optional.empty());
        assertFalse(service.existsByUserName("unknown"));
    }
}