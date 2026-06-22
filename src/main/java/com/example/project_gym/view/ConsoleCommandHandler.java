package com.example.project_gym.view;


import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.UserType;
import com.example.project_gym.model.dto.dtoin.LoginResultDto;
import com.example.project_gym.service.AuthorizationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.util.Locale;


@Component
public class ConsoleCommandHandler {

    private final GuestConsoleCommandService guestConsoleCommandService;
    private final AuthorizedProfileConsoleCommandService authorizedProfileConsoleCommandService;
    private final AuthorizationService authorizationService;
    private UserType currentUserType = UserType.UNKNOWN;
    private String authorizedUsername;

    public ConsoleCommandHandler(GuestConsoleCommandService guestConsoleCommandService,
                                 AuthorizedProfileConsoleCommandService authorizedProfileConsoleCommandService,
                                 AuthorizationService authorizationService) {
        this.guestConsoleCommandService = guestConsoleCommandService;
        this.authorizedProfileConsoleCommandService = authorizedProfileConsoleCommandService;
        this.authorizationService = authorizationService;
    }

    public String handle(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String trimmedInput = input.trim();
        String command = trimmedInput.toLowerCase(Locale.ROOT);

        if (guestConsoleCommandService.isExitCommand(command)
                || authorizedProfileConsoleCommandService.isExitCommand(command)) {
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
                    Trainer trainer = guestConsoleCommandService.createTrainer(trimmedInput);
                    yield "Trainer created: " + trainer.getUser().getUserName() +
                            " password: " + trainer.getUser().getPassword();
                }
                case "trainee-create" -> {
                    Trainee trainee = guestConsoleCommandService.createTrainee(trimmedInput);
                    yield "Trainee created: " + trainee.getUser().getUserName() +
                            " password: " + trainee.getUser().getPassword();
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
                case "trainer-select" -> "Selected trainer: " + authorizedProfileConsoleCommandService.selectTrainer(trimmedInput).getUser().getUserName();
                case "trainee-select" -> "Selected trainee: " + authorizedProfileConsoleCommandService.selectTrainee(trimmedInput).getUser().getUserName();
                case "trainee-password-change" -> {
                    authorizedProfileConsoleCommandService.changeTraineePassword(trimmedInput);
                    yield "Trainee password changed.";
                }
                case "trainer-password-change" -> {
                    authorizedProfileConsoleCommandService.changeTrainerPassword(trimmedInput);
                    yield "Trainer password changed.";
                }
                case "trainer-update" -> "Updated trainer: " + authorizedProfileConsoleCommandService.updateTrainer(trimmedInput).getId();
                case "trainee-update" -> "Updated trainee: " + authorizedProfileConsoleCommandService.updateTrainee(trimmedInput).getId();
                case "trainee-activate" -> "Activated trainee: " + authorizedProfileConsoleCommandService.activateTrainee(trimmedInput).getUser().getUserName();
                case "trainee-deactivate" -> "Deactivated trainee: " + authorizedProfileConsoleCommandService.deactivateTrainee(trimmedInput).getUser().getUserName();
                case "trainer-activate" -> "Activated trainer: " + authorizedProfileConsoleCommandService.activateTrainer(trimmedInput).getUser().getUserName();
                case "trainer-deactivate" -> "Deactivated trainer: " + authorizedProfileConsoleCommandService.deactivateTrainer(trimmedInput).getUser().getUserName();
                case "trainee-delete" -> authorizedProfileConsoleCommandService.deleteTrainee(trimmedInput)
                        ? "Trainee deleted."
                        : "Trainee not found.";
                case "trainee-trainings" -> "Trainee trainings: " + authorizedProfileConsoleCommandService.getTraineeTrainings(trimmedInput).size();
                case "trainer-trainings" -> "Trainer trainings: " + authorizedProfileConsoleCommandService.getTrainerTrainings(trimmedInput).size();
                case "training-add" -> "Created training: " + authorizedProfileConsoleCommandService.addTraining(trimmedInput).getId();
                case "trainee-unassigned-trainers" -> "Unassigned trainers: " + authorizedProfileConsoleCommandService.getUnassignedTrainers(trimmedInput).size();
                case "trainee-trainers-update" -> {
                    authorizedProfileConsoleCommandService.updateTraineesTrainers(trimmedInput);
                    yield "Trainee trainers list updated.";
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
        if (authorizedProfileConsoleCommandService.isSelectTrainerCommand(command)) {
            return "trainer-select";
        }
        if (authorizedProfileConsoleCommandService.isSelectTraineeCommand(command)) {
            return "trainee-select";
        }
        if (authorizedProfileConsoleCommandService.isTraineePasswordChangeCommand(command)) {
            return "trainee-password-change";
        }
        if (authorizedProfileConsoleCommandService.isTrainerPasswordChangeCommand(command)) {
            return "trainer-password-change";
        }
        if (authorizedProfileConsoleCommandService.isUpdateTrainerCommand(command)) {
            return "trainer-update";
        }
        if (authorizedProfileConsoleCommandService.isUpdateTraineeCommand(command)) {
            return "trainee-update";
        }
        if (authorizedProfileConsoleCommandService.isActivateTraineeCommand(command)) {
            return "trainee-activate";
        }
        if (authorizedProfileConsoleCommandService.isDeactivateTraineeCommand(command)) {
            return "trainee-deactivate";
        }
        if (authorizedProfileConsoleCommandService.isActivateTrainerCommand(command)) {
            return "trainer-activate";
        }
        if (authorizedProfileConsoleCommandService.isDeactivateTrainerCommand(command)) {
            return "trainer-deactivate";
        }
        if (authorizedProfileConsoleCommandService.isDeleteTraineeCommand(command)) {
            return "trainee-delete";
        }
        if (authorizedProfileConsoleCommandService.isGetTraineeTrainingsCommand(command)) {
            return "trainee-trainings";
        }
        if (authorizedProfileConsoleCommandService.isGetTrainerTrainingsCommand(command)) {
            return "trainer-trainings";
        }
        if (authorizedProfileConsoleCommandService.isAddTrainingCommand(command)) {
            return "training-add";
        }
        if (authorizedProfileConsoleCommandService.isGetUnassignedTrainersCommand(command)) {
            return "trainee-unassigned-trainers";
        }
        if (authorizedProfileConsoleCommandService.isUpdateTraineesTrainersCommand(command)) {
            return "trainee-trainers-update";
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
            LoginResultDto loginResultDto = new LoginResultDto(parts[1], parts[2]);
            currentUserType = authorizationService.authenticate(loginResultDto);
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
                trainer select <username>
                trainee select <username>
                trainee password-change <username> <oldPassword> <newPassword>
                trainer password-change <username> <oldPassword> <newPassword>
                trainer update <username> [specialization=<type>] [active=true|false]
                trainee update <username> [address=<value>] [active=true|false] [dateOfBirth=dd-MM-yyy]
                trainee activate <username> | trainee deactivate <username>
                trainer activate <username> | trainer deactivate <username>
                trainee delete <username>
                trainee trainings <username> [fromDate=dd-MM-yyy] [toDate=dd-MM-yyy] [trainerName=<name>] [trainingType=<type>]
                trainer trainings <username> [fromDate=dd-MM-yyy] [toDate=dd-MM-yyy] [traineeName=<name>]
                training add <traineeUsername> <trainerUsername> <trainingTypeName> <trainingName> <dd-MM-yyy> <durationMinutes>
                trainee unassigned-trainers <username>
                trainee trainers-update <username> <trainerUsername1,trainerUsername2,...>
                logout
                exit
                """;
    }
}