package com.example.project_gym.view;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import com.example.project_gym.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsoleCommandHandlerTest {

    @Mock
    private GymFacade gymFacade;

    private ConsoleCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ConsoleCommandHandler(gymFacade);
    }

    @Test
    void handle_shouldReturnHelp() {
        String output = handler.handle("help");

        assertTrue(output.contains("Commands:"));
        assertTrue(output.contains("trainer create"));
    }

    @Test
    void handle_shouldReturnExit() {
        assertEquals("exit", handler.handle("exit"));
    }

    @Test
    void handle_shouldCreateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        when(gymFacade.createTrainer("Ivan", "Ivanov", TrainingType.CARDIO)).thenReturn(trainer);

        String output = handler.handle("trainer create Ivan Ivanov CARDIO");

        assertTrue(output.startsWith("Created trainer:"));
        verify(gymFacade).createTrainer("Ivan", "Ivanov", TrainingType.CARDIO);
    }

    @Test
    void handle_shouldUpdateTrainerWithNullableFields() {
        when(gymFacade.updateTrainer(2L, null, null)).thenReturn(new Trainer());

        String output = handler.handle("trainer update 2 null null");

        assertTrue(output.startsWith("Updated trainer:"));
        verify(gymFacade).updateTrainer(2L, null, null);
    }

    @Test
    void handle_shouldCreateTraineeWithAddressContainingSpaces() {
        when(gymFacade.createTrainee(eq("Ann"), eq("Lee"), any(), eq("New York"))).thenReturn(new Trainee());

        String output = handler.handle("trainee create Ann Lee 2025-01-20 New York");

        assertTrue(output.startsWith("Created trainee:"));
        verify(gymFacade).createTrainee(eq("Ann"), eq("Lee"), any(), eq("New York"));
    }

    @Test
    void handle_shouldUpdateTraineeWithNullAddressByDash() {
        when(gymFacade.updateTrainee(3L, true, null)).thenReturn(new Trainee());

        String output = handler.handle("trainee update 3 true -");

        assertTrue(output.startsWith("Updated trainee:"));
        verify(gymFacade).updateTrainee(3L, true, null);
    }

    @Test
    void handle_shouldDeleteTrainee() {
        when(gymFacade.deleteTrainee(4L)).thenReturn(true);

        String output = handler.handle("trainee delete 4");

        assertEquals("Trainee deleted.", output);
        verify(gymFacade).deleteTrainee(4L);
    }

    @Test
    void handle_shouldCreateTraining() {
        when(gymFacade.createTraining(eq(1L), eq(2L), eq("Morning"), eq(TrainingType.CARDIO), any(), any()))
                .thenReturn(new Training());

        String output = handler.handle("training create 1 2 Morning CARDIO 2025-02-10 45");

        assertTrue(output.startsWith("Created training:"));
    }

    @Test
    void handle_shouldReturnErrorOnNotEnoughArgs() {
        String output = handler.handle("trainer create Ivan");

        assertTrue(output.startsWith("Error:"));
    }

    @Test
    void handle_shouldReturnUnknownCommand() {
        assertEquals("Unknown command. Type 'help'.", handler.handle("something else"));
    }
}

