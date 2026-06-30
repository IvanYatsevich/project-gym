package com.example.project_gym.view;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.domain.entity.UserType;
import com.example.project_gym.domain.entity.User;
import com.example.project_gym.model.request.LoginRequest;
import com.example.project_gym.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.AuthenticationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsoleCommandHandlerTest {

    @Mock
    private GuestConsoleCommandService guestConsoleCommandService;

    @Mock
    private AuthenticatedProfileConsoleCommandService authenticatedProfileConsoleCommandService;

    @Mock
    private AuthenticationService authenticationService;

    private ConsoleCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ConsoleCommandHandler(
                guestConsoleCommandService,
                authenticatedProfileConsoleCommandService,
                authenticationService
        );
    }

    @Test
    void handle_shouldReturnHelp() {
        when(guestConsoleCommandService.isHelpCommand("help")).thenReturn(true);
        when(guestConsoleCommandService.showHelp()).thenReturn("Commands:");

        String output = handler.handle("help");

        assertTrue(output.contains("Commands:"));
        assertTrue(output.contains("authorize <username> <password>"));
    }

    @Test
    void handle_shouldReturnExit() {
        when(guestConsoleCommandService.isExitCommand("exit")).thenReturn(true);

        assertEquals("exit", handler.handle("exit"));
    }

    @Test
    void handle_shouldReturnAuthorizeUsageForBareAuthorize() {
        when(guestConsoleCommandService.isHelpCommand("authorize")).thenReturn(false);
        when(guestConsoleCommandService.isAuthorizeCommand("authorize")).thenReturn(true);

        String output = handler.handle("authorize");

        assertEquals("Use: authorize <username> <password>", output);
    }

    @Test
    void handle_shouldAuthorizeAndSwitchToAuthorizedMode() throws AuthenticationException {
        when(guestConsoleCommandService.isHelpCommand("authorize john 123")).thenReturn(false);
        when(guestConsoleCommandService.isAuthorizeCommand("authorize john 123")).thenReturn(false);
        when(authenticationService.authenticate(new LoginRequest("john", "123"))).thenReturn(UserType.TRAINEE);

        String output = handler.handle("authorize john 123");

        assertTrue(output.contains("Authorized as TRAINEE: john"));
    }

    @Test
    void handle_shouldProcessGuestTrainerCreate() {
        TrainerEntity trainerEntity = new TrainerEntity();
        User user = new User();
        user.setUserName("Ivan.Ivanov");
        user.setPassword("secret");
        trainerEntity.setUser(user);

        when(guestConsoleCommandService.isHelpCommand("trainer create ivan ivanov cardio")).thenReturn(false);
        when(guestConsoleCommandService.isAuthorizeCommand("trainer create ivan ivanov cardio")).thenReturn(false);
        when(guestConsoleCommandService.isTrainerCreateCommand("trainer create ivan ivanov cardio")).thenReturn(true);
        when(guestConsoleCommandService.createTrainer("trainer create Ivan Ivanov CARDIO")).thenReturn(trainerEntity);

        String output = handler.handle("trainer create Ivan Ivanov CARDIO");

        assertTrue(output.startsWith("Trainer created:"));
    }

    @Test
    void handle_shouldProcessGuestTraineeCreate() {
        TraineeEntity traineeEntity = new TraineeEntity();
        User user = new User();
        user.setUserName("Ann.Lee");
        user.setPassword("secret");
        traineeEntity.setUser(user);

        when(guestConsoleCommandService.isHelpCommand("trainee create ann lee 2025-01-20 new york")).thenReturn(false);
        when(guestConsoleCommandService.isAuthorizeCommand("trainee create ann lee 2025-01-20 new york")).thenReturn(false);
        when(guestConsoleCommandService.isTrainerCreateCommand("trainee create ann lee 2025-01-20 new york")).thenReturn(false);
        when(guestConsoleCommandService.isTraineeCreateCommand("trainee create ann lee 2025-01-20 new york")).thenReturn(true);
        when(guestConsoleCommandService.createTrainee("trainee create Ann Lee 2025-01-20 New York")).thenReturn(traineeEntity);

        String output = handler.handle("trainee create Ann Lee 2025-01-20 New York");

        assertTrue(output.startsWith("Trainee created:"));
    }

    @Test
    void handle_shouldHandleAuthorizedHelpAfterLogin() throws AuthenticationException {
        when(guestConsoleCommandService.isHelpCommand("authorize john 123")).thenReturn(false);
        when(guestConsoleCommandService.isAuthorizeCommand("authorize john 123")).thenReturn(false);
        when(authenticationService.authenticate(new LoginRequest("john", "123"))).thenReturn(UserType.TRAINER);

        handler.handle("authorize john 123");
        String output = handler.handle("help");

        assertTrue(output.contains("Authorized commands:"));
        assertTrue(output.contains("logout"));
    }

    @Test
    void handle_shouldLogoutFromAuthorizedMode() throws AuthenticationException {
        when(guestConsoleCommandService.isHelpCommand("authorize john 123")).thenReturn(false);
        when(guestConsoleCommandService.isAuthorizeCommand("authorize john 123")).thenReturn(false);
        when(authenticationService.authenticate(new LoginRequest("john", "123"))).thenReturn(UserType.TRAINEE);

        handler.handle("authorize john 123");
        String output = handler.handle("logout");

        assertEquals("Logged out from TRAINEE profile.", output);
    }

    @Test
    void handle_shouldRouteTraineeTrainingsAndReturnReadableList() throws AuthenticationException {
        when(guestConsoleCommandService.isHelpCommand("authorize john 123")).thenReturn(false);
        when(guestConsoleCommandService.isAuthorizeCommand("authorize john 123")).thenReturn(false);
        when(authenticationService.authenticate(new LoginRequest("john", "123"))).thenReturn(UserType.TRAINEE);

        when(authenticatedProfileConsoleCommandService.isGetTraineeTrainingsCommand("trainee trainings hulk.hogan fromdate=12-11-2000"))
                .thenReturn(true);

        TrainingEntity trainingEntity = new TrainingEntity();
        trainingEntity.setTrainingName("MorningCardio");
        trainingEntity.setTrainingDate(LocalDateTime.of(2000, 11, 12, 0, 0));
        trainingEntity.setTrainingDuration(60L);

        TraineeEntity traineeEntity = new TraineeEntity();
        User traineeUser = new User();
        traineeUser.setUserName("Hulk.Hogan");
        traineeEntity.setUser(traineeUser);
        trainingEntity.setTraineeEntity(traineeEntity);

        TrainerEntity trainerEntity = new TrainerEntity();
        User trainerUser = new User();
        trainerUser.setUserName("Ivan.Ivanov");
        trainerEntity.setUser(trainerUser);
        trainingEntity.setTrainerEntity(trainerEntity);

        when(authenticatedProfileConsoleCommandService.getTraineeTrainings("trainee trainings Hulk.Hogan fromDate=12-11-2000"))
                .thenReturn(List.of(trainingEntity));

        handler.handle("authorize john 123");
        String output = handler.handle("trainee trainings Hulk.Hogan fromDate=12-11-2000");

        assertTrue(output.contains("Trainee trainings:"));
        assertTrue(output.contains("MorningCardio"));
        assertTrue(output.contains("trainer=Ivan.Ivanov"));
        assertTrue(output.contains("trainee=Hulk.Hogan"));
    }

    @Test
    void handle_shouldReturnUnassignedTrainerNamesInsteadOfCount() throws AuthenticationException {
        when(guestConsoleCommandService.isHelpCommand("authorize john 123")).thenReturn(false);
        when(guestConsoleCommandService.isAuthorizeCommand("authorize john 123")).thenReturn(false);
        when(authenticationService.authenticate(new LoginRequest("john", "123"))).thenReturn(UserType.TRAINEE);

        when(authenticatedProfileConsoleCommandService.isGetUnassignedTrainersCommand("trainee unassigned-trainers hulk.hogan"))
                .thenReturn(true);

        TrainerEntity firstTrainer = new TrainerEntity();
        User firstUser = new User();
        firstUser.setFirstName("Ivan");
        firstUser.setLastName("Ivanov");
        firstUser.setUserName("Ivan.Ivanov");
        firstTrainer.setUser(firstUser);

        TrainerEntity secondTrainer = new TrainerEntity();
        User secondUser = new User();
        secondUser.setFirstName("Mike");
        secondUser.setLastName("Tyson");
        secondUser.setUserName("Mike.Tyson");
        secondTrainer.setUser(secondUser);

        when(authenticatedProfileConsoleCommandService.getUnassignedTrainers("trainee unassigned-trainers Hulk.Hogan"))
                .thenReturn(List.of(firstTrainer, secondTrainer));

        handler.handle("authorize john 123");
        String output = handler.handle("trainee unassigned-trainers Hulk.Hogan");

        assertTrue(output.contains("Unassigned trainers:"));
        assertTrue(output.contains("Ivan Ivanov (Ivan.Ivanov)"));
        assertTrue(output.contains("Mike Tyson (Mike.Tyson)"));
    }

    @Test
    void handle_shouldReturnErrorOnNotEnoughArgs() {
        when(guestConsoleCommandService.isHelpCommand("trainer create ivan")).thenReturn(false);
        when(guestConsoleCommandService.isAuthorizeCommand("trainer create ivan")).thenReturn(false);
        when(guestConsoleCommandService.isTrainerCreateCommand("trainer create ivan")).thenReturn(true);
        when(guestConsoleCommandService.createTrainer("trainer create Ivan"))
                .thenThrow(new IllegalArgumentException("trainer create requires firstName, lastName and trainingTypeName."));

        String output = handler.handle("trainer create Ivan");

        assertTrue(output.startsWith("Error:"));
    }

    @Test
    void handle_shouldReturnUnknownCommand() {
        when(guestConsoleCommandService.isHelpCommand("something else")).thenReturn(false);
        when(guestConsoleCommandService.isAuthorizeCommand("something else")).thenReturn(false);
        when(guestConsoleCommandService.isTrainerCreateCommand("something else")).thenReturn(false);
        when(guestConsoleCommandService.isTraineeCreateCommand("something else")).thenReturn(false);

        assertEquals("Unknown command. Type 'help'.", handler.handle("something else"));
    }
}

