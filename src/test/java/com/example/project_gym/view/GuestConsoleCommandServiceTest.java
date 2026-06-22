package com.example.project_gym.view;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.dto.dtoin.TraineeDtoIn;
import com.example.project_gym.model.dto.dtoin.TrainerDtoIn;
import com.example.project_gym.service.TraineeService;
import com.example.project_gym.service.TrainerService;
import com.example.project_gym.utilservices.unauthservices.ProfileCreationParserService;
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
        TrainerDtoIn dto = new TrainerDtoIn("Ivan", "Ivanov", "CARDIO");
        Trainer trainer = new Trainer();
        when(parser.parseTrainerCreate("trainer create Ivan Ivanov CARDIO")).thenReturn(dto);
        when(trainerService.create(dto)).thenReturn(trainer);

        Trainer result = service.createTrainer("trainer create Ivan Ivanov CARDIO");

        assertSame(trainer, result);
        verify(trainerService).create(dto);
    }

    @Test
    void createTrainee_shouldDelegate() {
        TraineeDtoIn dto = new TraineeDtoIn("Hulk", "Hogan", null, null);
        Trainee trainee = new Trainee();
        when(parser.parseTraineeCreate("trainee create Hulk Hogan")).thenReturn(dto);
        when(traineeService.create(dto)).thenReturn(trainee);

        Trainee result = service.createTrainee("trainee create Hulk Hogan");

        assertSame(trainee, result);
        verify(traineeService).create(dto);
    }
}

