package com.example.project_gym.view;


import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.UserType;
import com.example.project_gym.model.request.LoginRequest;
import com.example.project_gym.service.AuthenticationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.util.Locale;


@Component
public class ConsoleCommandHandler {

    private final GuestConsoleCommandService guestConsoleCommandService;
    private final AuthenticatedProfileConsoleCommandService authenticatedProfileConsoleCommandService;
    private final AuthenticationService authenticationService;
    private UserType currentUserType = UserType.UNKNOWN;
    private String authorizedUsername;

    public ConsoleCommandHandler(GuestConsoleCommandService guestConsoleCommandService,
                                 AuthenticatedProfileConsoleCommandService authenticatedProfileConsoleCommandService,
                                 AuthenticationService authenticationService) {
        this.guestConsoleCommandService = guestConsoleCommandService;
        this.authenticatedProfileConsoleCommandService = authenticatedProfileConsoleCommandService;
        this.authenticationService = authenticationService;
    }

    public String handle(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String trimmedInput = input.trim();
        String command = trimmedInput.toLowerCase(Locale.ROOT);

        if (guestConsoleCommandService.isExitCommand(command)
                || authenticatedProfileConsoleCommandService.isExitCommand(command)) {
            return "exit";
        }

        if (isAuthorized()) {
            return handleAuthorizedCommand(trimmedInput, command);
        }

        return handleGuestCommand(trimmedInput, command);
    }

    private String handleGuestCommand(String trimmedInput, String command) {
        String commandKey = resolveGuestCommandKey(command);

        try {
            return switch (commandKey) {
                case "help" -> guestConsoleCommandService.showHelp() + "\nauthorize <username> <password> - Login with credentials";
                case "authorize-usage" -> "Use: authorize <username> <password>";
                case "authorize" -> authorize(trimmedInput);
                case "trainerEntity-create" -> {
                    TrainerEntity trainerEntity = guestConsoleCommandService.createTrainer(trimmedInput);
                    yield "Trainer created: " + trainerEntity.getUser().getUserName() +
                            " password: " + trainerEntity.getUser().getPassword();
                }
                case "traineeEntity-create" -> {
                    TraineeEntity traineeEntity = guestConsoleCommandService.createTrainee(trimmedInput);
                    yield "Trainee created: " + traineeEntity.getUser().getUserName() +
                            " password: " + traineeEntity.getUser().getPassword();
                }
                default -> "Unknown command. Type 'help'.";
            };
        } catch (IllegalArgumentException | IllegalStateException | EntityNotFoundException e) {
            return "Error: " + e.getMessage();
        }
    }

    private String handleAuthorizedCommand(String trimmedInput, String command) {
        String commandKey = resolveAuthorizedCommandKey(command);
        try {
            return switch (commandKey) {
                case "help" -> showAuthorizedHelp();
                case "logout" -> logout();
                case "trainerEntity-select" -> "Selected trainerEntity: " + authenticatedProfileConsoleCommandService.selectTrainer(trimmedInput).getUser().getUserName();
                case "traineeEntity-select" -> "Selected traineeEntity: " + authenticatedProfileConsoleCommandService.selectTrainee(trimmedInput).getUser().getUserName();
                case "traineeEntity-password-change" -> {
                    authenticatedProfileConsoleCommandService.changeTraineePassword(trimmedInput);
                    yield "Trainee password changed.";
                }
                case "trainerEntity-password-change" -> {
                    authenticatedProfileConsoleCommandService.changeTrainerPassword(trimmedInput);
                    yield "Trainer password changed.";
                }
                case "trainerEntity-update" -> "Updated trainerEntity: " + authenticatedProfileConsoleCommandService.updateTrainer(trimmedInput).getId();
                case "traineeEntity-update" -> "Updated traineeEntity: " + authenticatedProfileConsoleCommandService.updateTrainee(trimmedInput).getId();
                case "traineeEntity-activate" -> "Activated traineeEntity: " + authenticatedProfileConsoleCommandService.activateTrainee(trimmedInput).getUser().getUserName();
                case "traineeEntity-deactivate" -> "Deactivated traineeEntity: " + authenticatedProfileConsoleCommandService.deactivateTrainee(trimmedInput).getUser().getUserName();
                case "trainerEntity-activate" -> "Activated trainerEntity: " + authenticatedProfileConsoleCommandService.activateTrainer(trimmedInput).getUser().getUserName();
                case "trainerEntity-deactivate" -> "Deactivated trainerEntity: " + authenticatedProfileConsoleCommandService.deactivateTrainer(trimmedInput).getUser().getUserName();
                case "traineeEntity-delete" -> authenticatedProfileConsoleCommandService.deleteTrainee(trimmedInput)
                        ? "Trainee deleted."
                        : "Trainee not found.";
                case "traineeEntity-trainingEntities" -> "Trainee trainingEntities: " + authenticatedProfileConsoleCommandService.getTraineeTrainings(trimmedInput).size();
                case "trainerEntity-trainingEntities" -> "Trainer trainingEntities: " + authenticatedProfileConsoleCommandService.getTrainerTrainings(trimmedInput).size();
                case "trainingEntity-add" -> "Created trainingEntity: " + authenticatedProfileConsoleCommandService.addTraining(trimmedInput).getId();
                case "traineeEntity-unassigned-trainerEntities" -> "Unassigned trainerEntities: " + authenticatedProfileConsoleCommandService.getUnassignedTrainers(trimmedInput).size();
                case "traineeEntity-trainerEntities-update" -> {
                    authenticatedProfileConsoleCommandService.updateTraineesTrainers(trimmedInput);
                    yield "Trainee trainerEntities list updated.";
                }
                default -> "Unknown authorized command. Type 'help'.";
            };
        } catch (IllegalArgumentException | IllegalStateException | EntityNotFoundException ex) {
            return "Error: " + ex.getMessage();
        }
    }

    private String resolveGuestCommandKey(String command) {
        if (guestConsoleCommandService.isHelpCommand(command)) {
            return "help";
        }
        if (guestConsoleCommandService.isAuthorizeCommand(command)) {
            return "authorize-usage";
        }
        if (command.startsWith("authorize ")) {
            return "authorize";
        }
        if (guestConsoleCommandService.isTrainerCreateCommand(command)) {
            return "trainerEntity-create";
        }
        if (guestConsoleCommandService.isTraineeCreateCommand(command)) {
            return "traineeEntity-create";
        }
        return "unknown";
    }

    private String resolveAuthorizedCommandKey(String command) {
        if ("help".equals(command)) {
            return "help";
        }
        if ("logout".equals(command)) {
            return "logout";
        }
        if (authenticatedProfileConsoleCommandService.isSelectTrainerCommand(command)) {
            return "trainerEntity-select";
        }
        if (authenticatedProfileConsoleCommandService.isSelectTraineeCommand(command)) {
            return "traineeEntity-select";
        }
        if (authenticatedProfileConsoleCommandService.isTraineePasswordChangeCommand(command)) {
            return "traineeEntity-password-change";
        }
        if (authenticatedProfileConsoleCommandService.isTrainerPasswordChangeCommand(command)) {
            return "trainerEntity-password-change";
        }
        if (authenticatedProfileConsoleCommandService.isUpdateTrainerCommand(command)) {
            return "trainerEntity-update";
        }
        if (authenticatedProfileConsoleCommandService.isUpdateTraineeCommand(command)) {
            return "traineeEntity-update";
        }
        if (authenticatedProfileConsoleCommandService.isActivateTraineeCommand(command)) {
            return "traineeEntity-activate";
        }
        if (authenticatedProfileConsoleCommandService.isDeactivateTraineeCommand(command)) {
            return "traineeEntity-deactivate";
        }
        if (authenticatedProfileConsoleCommandService.isActivateTrainerCommand(command)) {
            return "trainerEntity-activate";
        }
        if (authenticatedProfileConsoleCommandService.isDeactivateTrainerCommand(command)) {
            return "trainerEntity-deactivate";
        }
        if (authenticatedProfileConsoleCommandService.isDeleteTraineeCommand(command)) {
            return "traineeEntity-delete";
        }
        if (authenticatedProfileConsoleCommandService.isGetTraineeTrainingsCommand(command)) {
            return "traineeEntity-trainingEntities";
        }
        if (authenticatedProfileConsoleCommandService.isGetTrainerTrainingsCommand(command)) {
            return "trainerEntity-trainingEntities";
        }
        if (authenticatedProfileConsoleCommandService.isAddTrainingCommand(command)) {
            return "trainingEntity-add";
        }
        if (authenticatedProfileConsoleCommandService.isGetUnassignedTrainersCommand(command)) {
            return "traineeEntity-unassigned-trainerEntities";
        }
        if (authenticatedProfileConsoleCommandService.isUpdateTraineesTrainersCommand(command)) {
            return "traineeEntity-trainerEntities-update";
        }
        return "unknown";
    }

    private String logout() {
        UserType previousType = currentUserType;
        authorizedUsername = null;
        currentUserType = UserType.UNKNOWN;
        return "Logged out from " + previousType + " profile.";
    }

    private String authorize(String trimmedInput) {
        String[] parts = trimmedInput.split("\\s+");
        if (parts.length != 3) {
            return "Error: authorize <username> <password>";
        }

        try {
            LoginRequest loginRequest = new LoginRequest(parts[1], parts[2]);
            currentUserType = authenticationService.authenticate(loginRequest);
            authorizedUsername = parts[1];
            return "Authorized as " + currentUserType + ": " + authorizedUsername + ". Type 'help' for available commands.";
        } catch (AuthenticationException e) {
            return "Error: " + e.getMessage();
        }
    }

    private boolean isAuthorized() {
        return currentUserType == UserType.TRAINER || currentUserType == UserType.TRAINEE;
    }

    private String showAuthorizedHelp() {
        return """
                Authorized commands:
                trainerEntity select <username>
                traineeEntity select <username>
                traineeEntity password-change <username> <oldPassword> <newPassword>
                trainerEntity password-change <username> <oldPassword> <newPassword>
                trainerEntity update <username> [specialization=<type>] [active=true|false]
                traineeEntity update <username> [address=<value>] [active=true|false] [dateOfBirth=dd-MM-yyy]
                traineeEntity activate <username> | traineeEntity deactivate <username>
                trainerEntity activate <username> | trainerEntity deactivate <username>
                traineeEntity delete <username>
                traineeEntity trainingEntities <username> [fromDate=dd-MM-yyy] [toDate=dd-MM-yyy] [trainerName=<name>] [trainingType=<type>]
                trainerEntity trainingEntities <username> [fromDate=dd-MM-yyy] [toDate=dd-MM-yyy] [traineeName=<name>]
                trainingEntity add <traineeUsername> <trainerUsername> <trainingTypeName> <trainingName> <dd-MM-yyy> <durationMinutes>
                traineeEntity unassigned-trainerEntities <username>
                traineeEntity trainerEntities-update <username> <trainerUsername1,trainerUsername2,...>
                logout
                exit
                """;
    }
}