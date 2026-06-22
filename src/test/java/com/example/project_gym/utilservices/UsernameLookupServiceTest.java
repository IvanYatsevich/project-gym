package com.example.project_gym.utilservices;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.repository.idao.ITraineeDAO;
import com.example.project_gym.repository.idao.ITrainerDAO;
import com.example.project_gym.utilservices.unauthservices.username.UsernameLookupService;
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
    private ITrainerDAO trainerDao;
    @Mock
    private ITraineeDAO traineeDao;

    private UsernameLookupService service;

    @BeforeEach
    void setUp() {
        service = new UsernameLookupService();
        service.setTrainerDao(trainerDao);
        service.setTraineeDao(traineeDao);
    }

    @Test
    void existsByUserName_shouldReturnTrueWhenTrainerExists() {
        when(trainerDao.selectByUsername("ivan")).thenReturn(Optional.of(new Trainer()));

        assertTrue(service.existsByUserName("ivan"));
    }

    @Test
    void existsByUserName_shouldReturnTrueWhenTraineeExists() {
        when(trainerDao.selectByUsername("hulk")).thenReturn(Optional.empty());
        when(traineeDao.selectByUsername("hulk")).thenReturn(Optional.of(new Trainee()));

        assertTrue(service.existsByUserName("hulk"));
    }

    @Test
    void existsByUserName_shouldReturnFalseWhenNotFound() {
        when(trainerDao.selectByUsername("none")).thenReturn(Optional.empty());
        when(traineeDao.selectByUsername("none")).thenReturn(Optional.empty());

        assertFalse(service.existsByUserName("none"));
    }
}
