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

import static org.junit.jupiter.api.Assertions.*;
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
    void existsByUserName_shouldReturnTrueWhenTrainerExists() {
        when(trainerDao.findByUsername("ivan")).thenReturn(Optional.of(new TrainerEntity()));

        assertTrue(service.existsByUserName("ivan"));
    }

    @Test
    void existsByUserName_shouldReturnTrueWhenTraineeExists() {
        when(trainerDao.findByUsername("hulk")).thenReturn(Optional.empty());
        when(traineeDao.findByUsername("hulk")).thenReturn(Optional.of(new TraineeEntity()));

        assertTrue(service.existsByUserName("hulk"));
    }

    @Test
    void existsByUserName_shouldReturnFalseWhenNotFound() {
        when(trainerDao.findByUsername("none")).thenReturn(Optional.empty());
        when(traineeDao.findByUsername("none")).thenReturn(Optional.empty());

        assertFalse(service.existsByUserName("none"));
    }
}
