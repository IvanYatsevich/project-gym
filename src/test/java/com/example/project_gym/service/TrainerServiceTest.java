package com.example.project_gym.service;

import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.TrainingType;
import com.example.project_gym.model.dto.dtoin.TrainerDtoIn;
import com.example.project_gym.model.dto.dtoupdate.TrainerUpdateDto;
import com.example.project_gym.repository.TrainerDaoImpl;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerDaoImpl trainerDao;
    @Mock
    private UserIdGenerator idGenerator;
    @Mock
    private UniqueUserNameGenerator userNameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;

    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerService();
        ReflectionTestUtils.setField(trainerService, "trainerDao", trainerDao);
        trainerService.setIdGenerator(idGenerator);
        trainerService.setUserNameGenerator(userNameGenerator);
        trainerService.setPasswordGenerator(passwordGenerator);
    }

    @Test
    void create_shouldFillAndPersistTrainer() {
        TrainerDtoIn dto = new TrainerDtoIn("Petr", "Petrov", TrainingType.CARDIO);

        when(idGenerator.generateId()).thenReturn(7L);
        when(userNameGenerator.generateUnique("Petr", "Petrov")).thenReturn("Petr.Petrov");
        when(passwordGenerator.generatePassword()).thenReturn("secret");
        when(trainerDao.create(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.create(dto);

        assertEquals(7L, result.getId());
        assertEquals("Petr", result.getFirstName());
        assertEquals("Petrov", result.getLastName());
        assertEquals("Petr.Petrov", result.getUserName());
        assertEquals("secret", result.getPassword());
        assertEquals(TrainingType.CARDIO, result.getTrainingType());
        assertTrue(result.isActive());

        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerDao).create(captor.capture());
        assertEquals(7L, captor.getValue().getId());
    }

    @Test
    void select_shouldReturnTrainerWhenExists() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        when(trainerDao.selectById(1L)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.select(1L);

        assertSame(trainer, result);
    }

    @Test
    void select_shouldThrowWhenMissing() {
        when(trainerDao.selectById(100L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> trainerService.select(100L));

        assertTrue(ex.getMessage().contains("100"));
    }

    @Test
    void update_shouldApplyProvidedFieldsOnly() {
        Trainer existing = new Trainer();
        existing.setId(2L);
        existing.setActive(false);
        existing.setTrainingType(TrainingType.CARDIO);

        when(trainerDao.selectById(2L)).thenReturn(Optional.of(existing));
        when(trainerDao.update(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer updated = trainerService.update(2L, new TrainerUpdateDto(true, TrainingType.STRENGTH));

        assertTrue(updated.isActive());
        assertEquals(TrainingType.STRENGTH, updated.getTrainingType());
        verify(trainerDao).update(existing);
    }
}

