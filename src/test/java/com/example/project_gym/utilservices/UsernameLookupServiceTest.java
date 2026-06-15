package com.example.project_gym.utilservices;

import com.example.project_gym.repository.TraineeDaoImpl;
import com.example.project_gym.repository.TrainerDaoImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameLookupServiceTest {

    @Mock
    private TrainerDaoImpl trainerDao;
    @Mock
    private TraineeDaoImpl traineeDao;

    private UsernameLookupService service;

    @BeforeEach
    void setUp() {
        service = new UsernameLookupService();
        service.setTrainerDao(trainerDao);
        service.setTraineeDao(traineeDao);
    }

    @Test
    void existsByUserName_shouldReturnTrueWhenFoundInTrainer() {
        when(trainerDao.existsByUserName("u")).thenReturn(true);

        assertTrue(service.existsByUserName("u"));
    }

    @Test
    void existsByUserName_shouldReturnTrueWhenFoundInTrainee() {
        when(trainerDao.existsByUserName("u")).thenReturn(false);
        when(traineeDao.existsByUserName("u")).thenReturn(true);

        assertTrue(service.existsByUserName("u"));
    }

    @Test
    void existsByUserName_shouldReturnFalseWhenNotFoundAnywhere() {
        when(trainerDao.existsByUserName("u")).thenReturn(false);
        when(traineeDao.existsByUserName("u")).thenReturn(false);

        assertFalse(service.existsByUserName("u"));
    }
}

