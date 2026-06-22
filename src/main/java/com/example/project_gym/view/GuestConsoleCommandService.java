package com.example.project_gym.view;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.dto.dtoin.TraineeDtoIn;
import com.example.project_gym.model.dto.dtoin.TrainerDtoIn;
import com.example.project_gym.service.TraineeService;
import com.example.project_gym.service.TrainerService;
import com.example.project_gym.utilservices.unauthservices.ProfileCreationParserService;
import org.springframework.stereotype.Component;

@Component
public class GuestConsoleCommandService {

    private final ProfileCreationParserService profileCreationParserService;
    private final TrainerService trainerService;
    private final TraineeService traineeService;

    public GuestConsoleCommandService(ProfileCreationParserService profileCreationParserService,
                                      TrainerService trainerService,
                                      TraineeService traineeService) {
        this.profileCreationParserService = profileCreationParserService;
        this.trainerService = trainerService;
        this.traineeService = traineeService;
    }

    public String showHelp() {
        return """
                Commands:
                trainer create <firstName> <lastName> <trainingTypeName> - Create trainer profile
                trainee create <firstName> <lastName> [dateOfBirth=dd-MM-yyy] [address] - Create trainee profile
                authorize - Login to the system
                exit - Close the application
                help - Show available commands
                """;
    }

    public boolean isHelpCommand(String command) {
        return "help".equals(command);
    }

    public boolean isExitCommand(String command) {
        return "exit".equals(command);
    }

    public boolean isAuthorizeCommand(String command) {
        return "authorize".equals(command);
    }

    public boolean isTrainerCreateCommand(String command) {
        return command.startsWith("trainer create ");
    }

    public boolean isTraineeCreateCommand(String command) {
        return command.startsWith("trainee create ");
    }

    public Trainer createTrainer(String input) {
        TrainerDtoIn dto = profileCreationParserService.parseTrainerCreate(input);
        return trainerService.create(dto);
    }

    public Trainee createTrainee(String input) {
        TraineeDtoIn dto = profileCreationParserService.parseTraineeCreate(input);
        return traineeService.create(dto);
    }
}

