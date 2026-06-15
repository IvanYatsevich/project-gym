package com.example.project_gym.service;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.dto.dtoin.TraineeDtoIn;
import com.example.project_gym.model.dto.dtoupdate.TraineeUpdateDto;
import com.example.project_gym.repository.TraineeDaoImpl;
import com.example.project_gym.utilservices.PasswordGenerator;
import com.example.project_gym.utilservices.UniqueUserNameGenerator;
import com.example.project_gym.utilservices.UserIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeDaoImpl traineeDao;
    @Mock
    private UserIdGenerator idGenerator;
    @Mock
    private UniqueUserNameGenerator userNameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;

    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeService = new TraineeService();
        ReflectionTestUtils.setField(traineeService, "traineeDao", traineeDao);
        traineeService.setIdGenerator(idGenerator);
        traineeService.setUserNameGenerator(userNameGenerator);
        traineeService.setPasswordGenerator(passwordGenerator);
    }

    @Test
    void create_shouldFillAndPersistTrainee() {
        Date dob = new Date();
        TraineeDtoIn dto = new TraineeDtoIn("Ivan", "Ivanov", dob, "Minsk");

        when(idGenerator.generateId()).thenReturn(10L);
        when(userNameGenerator.generateUnique("Ivan", "Ivanov")).thenReturn("Ivan.Ivanov");
        when(passwordGenerator.generatePassword()).thenReturn("pass123");
        when(traineeDao.create(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee result = traineeService.create(dto);

        assertEquals(10L, result.getId());
        assertEquals(10L, result.getUserId());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Ivanov", result.getLastName());
        assertEquals("Ivan.Ivanov", result.getUserName());
        assertEquals("pass123", result.getPassword());
        assertTrue(result.isActive());
        assertEquals("Minsk", result.getAddress());
        assertEquals(dob, result.getDateOfBirth());

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeDao).create(captor.capture());
        assertEquals(10L, captor.getValue().getId());
    }

    @Test
    void select_shouldReturnTraineeWhenExists() {
        Trainee trainee = new Trainee();
        trainee.setId(5L);
        when(traineeDao.selectById(5L)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.select(5L);

        assertSame(trainee, result);
    }

    @Test
    void select_shouldThrowWhenMissing() {
        when(traineeDao.selectById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> traineeService.select(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void update_shouldApplyProvidedFieldsOnly() {
        Trainee existing = new Trainee();
        existing.setId(8L);
        existing.setActive(false);
        existing.setAddress("Old");

        when(traineeDao.selectById(8L)).thenReturn(Optional.of(existing));
        when(traineeDao.update(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee updated = traineeService.update(8L, new TraineeUpdateDto(true, "New"));

        assertTrue(updated.isActive());
        assertEquals("New", updated.getAddress());
        verify(traineeDao).update(existing);
    }

    @Test
    void delete_shouldDeleteByExistingId() {
        Trainee existing = new Trainee();
        existing.setId(3L);

        when(traineeDao.selectById(3L)).thenReturn(Optional.of(existing));
        when(traineeDao.delete(3L)).thenReturn(true);

        boolean result = traineeService.delete(3L);

        assertTrue(result);
        verify(traineeDao).delete(3L);
    }
}

