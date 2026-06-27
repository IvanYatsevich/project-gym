package com.example.project_gym.view;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.model.request.CreateTraineeRequest;
import com.example.project_gym.model.request.CreateTrainerRequest;
import com.example.project_gym.service.TraineeService;
import com.example.project_gym.service.TrainerService;
import com.example.project_gym.utilservices.guestservices.ProfileCreationParserService;
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

    public TrainerEntity createTrainer(String input) {
        CreateTrainerRequest dto = profileCreationParserService.parseTrainerCreate(input);
        return trainerService.create(dto);
    }

    public TraineeEntity createTrainee(String input) {
        CreateTraineeRequest dto = profileCreationParserService.parseTraineeCreate(input);
        return traineeService.create(dto);
    }
}

