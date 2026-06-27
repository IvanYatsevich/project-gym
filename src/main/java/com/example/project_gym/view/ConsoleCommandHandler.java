package com.example.project_gym.view;


import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.domain.entity.UserType;
import com.example.project_gym.model.request.LoginRequest;
import com.example.project_gym.service.AuthenticationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


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
                case "trainer-create" -> {
                    TrainerEntity trainerEntity = guestConsoleCommandService.createTrainer(trimmedInput);
                    yield "Trainer created: " + trainerEntity.getUser().getUserName() +
                            " password: " + trainerEntity.getUser().getPassword();
                }
                case "trainee-create" -> {
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
                case "trainer-select" -> "Selected trainer: " + authenticatedProfileConsoleCommandService.selectTrainer(trimmedInput).getUser().getUserName();
                case "trainee-select" -> "Selected trainee: " + authenticatedProfileConsoleCommandService.selectTrainee(trimmedInput).getUser().getUserName();
                case "trainee-password-change" -> {
                    authenticatedProfileConsoleCommandService.changeTraineePassword(trimmedInput);
                    yield "Trainee password changed.";
                }
                case "trainer-password-change" -> {
                    authenticatedProfileConsoleCommandService.changeTrainerPassword(trimmedInput);
                    yield "Trainer password changed.";
                }
                case "trainer-update" -> "Updated trainer: " + authenticatedProfileConsoleCommandService.updateTrainer(trimmedInput).getId();
                case "trainee-update" -> "Updated trainee: " + authenticatedProfileConsoleCommandService.updateTrainee(trimmedInput).getId();
                case "trainee-activate" -> "Activated trainee: " + authenticatedProfileConsoleCommandService.activateTrainee(trimmedInput).getUser().getUserName();
                case "trainee-deactivate" -> "Deactivated trainee: " + authenticatedProfileConsoleCommandService.deactivateTrainee(trimmedInput).getUser().getUserName();
                case "trainer-activate" -> "Activated trainer: " + authenticatedProfileConsoleCommandService.activateTrainer(trimmedInput).getUser().getUserName();
                case "trainer-deactivate" -> "Deactivated trainer: " + authenticatedProfileConsoleCommandService.deactivateTrainer(trimmedInput).getUser().getUserName();
                case "trainee-delete" -> authenticatedProfileConsoleCommandService.deleteTrainee(trimmedInput)
                        ? "Trainee deleted."
                        : "Trainee not found.";
                case "trainee-trainings" -> formatTraineeTrainings(trimmedInput);
                case "trainer-trainings" -> formatTrainerTrainings(trimmedInput);
                case "training-add" -> "Created training: " + authenticatedProfileConsoleCommandService.addTraining(trimmedInput).getId();
                case "trainee-unassigned-trainers" -> formatUnassignedTrainers(trimmedInput);
                case "trainee-trainers-update" -> {
                    authenticatedProfileConsoleCommandService.updateTraineesTrainers(trimmedInput);
                    yield "Trainee trainer list updated.";
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
            return "trainer-create";
        }
        if (guestConsoleCommandService.isTraineeCreateCommand(command)) {
            return "trainee-create";
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
            return "trainer-select";
        }
        if (authenticatedProfileConsoleCommandService.isSelectTraineeCommand(command)) {
            return "trainee-select";
        }
        if (authenticatedProfileConsoleCommandService.isTraineePasswordChangeCommand(command)) {
            return "trainee-password-change";
        }
        if (authenticatedProfileConsoleCommandService.isTrainerPasswordChangeCommand(command)) {
            return "trainer-password-change";
        }
        if (authenticatedProfileConsoleCommandService.isUpdateTrainerCommand(command)) {
            return "trainer-update";
        }
        if (authenticatedProfileConsoleCommandService.isUpdateTraineeCommand(command)) {
            return "trainee-update";
        }
        if (authenticatedProfileConsoleCommandService.isActivateTraineeCommand(command)) {
            return "trainee-activate";
        }
        if (authenticatedProfileConsoleCommandService.isDeactivateTraineeCommand(command)) {
            return "trainee-deactivate";
        }
        if (authenticatedProfileConsoleCommandService.isActivateTrainerCommand(command)) {
            return "trainer-activate";
        }
        if (authenticatedProfileConsoleCommandService.isDeactivateTrainerCommand(command)) {
            return "trainer-deactivate";
        }
        if (authenticatedProfileConsoleCommandService.isDeleteTraineeCommand(command)) {
            return "trainee-delete";
        }
        if (authenticatedProfileConsoleCommandService.isGetTraineeTrainingsCommand(command)) {
            return "trainee-trainings";
        }
        if (authenticatedProfileConsoleCommandService.isGetTrainerTrainingsCommand(command)) {
            return "trainer-trainings";
        }
        if (authenticatedProfileConsoleCommandService.isAddTrainingCommand(command)) {
            return "training-add";
        }
        if (authenticatedProfileConsoleCommandService.isGetUnassignedTrainersCommand(command)) {
            return "trainee-unassigned-trainers";
        }
        if (authenticatedProfileConsoleCommandService.isUpdateTraineesTrainersCommand(command)) {
            return "trainee-trainers-update";
        }
        return "unknown";
    }

    private String logout() {
        UserType previousType = currentUserType;
        authenticationService.logout();
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

    private String formatTraineeTrainings(String command) {
        return formatTrainings(
                "Trainee trainings",
                authenticatedProfileConsoleCommandService.getTraineeTrainings(command)
        );
    }

    private String formatTrainerTrainings(String command) {
        return formatTrainings(
                "Trainer trainings",
                authenticatedProfileConsoleCommandService.getTrainerTrainings(command)
        );
    }

    private String formatUnassignedTrainers(String command) {
        List<TrainerEntity> trainers = authenticatedProfileConsoleCommandService.getUnassignedTrainers(command);
        if (trainers.isEmpty()) {
            return "Unassigned trainers: none";
        }

        String formatted = trainers.stream()
                .map(trainer -> trainer.getUser().getFirstName() + " "
                        + trainer.getUser().getLastName() + " (" + trainer.getUser().getUserName() + ")")
                .collect(Collectors.joining(", "));
        return "Unassigned trainers: " + formatted;
    }

    private String formatTrainings(String label, List<TrainingEntity> trainings) {
        if (trainings.isEmpty()) {
            return label + ": none";
        }

        String formatted = trainings.stream()
                .map(training -> training.getTrainingName()
                        + " [trainer=" + training.getTrainerEntity().getUser().getUserName()
                        + ", trainee=" + training.getTraineeEntity().getUser().getUserName()
                        + ", date=" + training.getTrainingDate()
                        + ", duration=" + training.getTrainingDuration() + " min]")
                .collect(Collectors.joining("; "));
        return label + ": " + formatted;
    }

    private String showAuthorizedHelp() {
        return """
                Authorized commands:
                trainer select <username>
                trainee select <username>
                trainee password-change <username> <oldPassword> <newPassword>
                trainer password-change <username> <oldPassword> <newPassword>
                trainer update <username> [specialization=<type>] [active=true|false]
                trainee update <username> [address=<value>] [active=true|false] [dateOfBirth=dd-MM-yyyy]
                trainee activate <username> | trainee deactivate <username>
                trainer activate <username> | trainer deactivate <username>
                trainee delete <username>
                trainee trainings <username> [fromDate=dd-MM-yyyy] [toDate=dd-MM-yyyy] [trainerName=<name>] [trainingType=<type>]
                trainer trainings <username> [fromDate=dd-MM-yyyy] [toDate=dd-MM-yyyy] [traineeName=<name>]
                training add <traineeUsername> <trainerUsername> <trainingTypeName> <trainingName> <dd-MM-yyyy> <durationMinutes>
                trainee unassigned-trainers <username>
                trainee trainers-update <username> <trainerUsername1,trainerUsername2,...>
                logout
                exit
                """;
    }
}