package com.example.project_gym.view;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.model.request.CreateTraineeRequest;
import com.example.project_gym.model.request.CreateTrainerRequest;
import com.example.project_gym.service.TraineeService;
import com.example.project_gym.service.TrainerService;
import com.example.project_gym.utilservices.guestservices.ProfileCreationParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GuestConsoleCommandServiceTest {

    @Mock
    private ProfileCreationParserService parser;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TraineeService traineeService;

    private GuestConsoleCommandService service;

    @BeforeEach
    void setUp() {
        service = new GuestConsoleCommandService(parser, trainerService, traineeService);
    }

    @Test
    void showHelp_shouldContainCommands() {
        assertTrue(service.showHelp().contains("trainer create"));
    }

    @Test
    void createTrainer_shouldDelegate() {
        CreateTrainerRequest dto = new CreateTrainerRequest("Ivan", "Ivanov", "CARDIO");
        TrainerEntity trainerEntity = new TrainerEntity();
        when(parser.parseTrainerCreate("trainer create Ivan Ivanov CARDIO")).thenReturn(dto);
        when(trainerService.create(dto)).thenReturn(trainerEntity);

        TrainerEntity result = service.createTrainer("trainer create Ivan Ivanov CARDIO");

        assertSame(trainerEntity, result);
        verify(trainerService).create(dto);
    }

    @Test
    void createTrainee_shouldDelegate() {
        CreateTraineeRequest dto = new CreateTraineeRequest("Hulk", "Hogan", null, null);
        TraineeEntity traineeEntity = new TraineeEntity();
        when(parser.parseTraineeCreate("trainee create Hulk Hogan")).thenReturn(dto);
        when(traineeService.create(dto)).thenReturn(traineeEntity);

        TraineeEntity result = service.createTrainee("trainee create Hulk Hogan");

        assertSame(traineeEntity, result);
        verify(traineeService).create(dto);
    }
}

