package com.example.project_gym.view;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.domain.entity.TrainingType;
import com.example.project_gym.model.request.PasswordChangeRequest;
import com.example.project_gym.model.request.CreateTrainingRequest;
import com.example.project_gym.model.request.TraineeTrainingsFilterRequest;
import com.example.project_gym.model.request.TrainerTrainingsFilterRequest;
import com.example.project_gym.model.request.UpdateTraineeRequest;
import com.example.project_gym.model.request.UpdateTrainerRequest;
import com.example.project_gym.repository.idao.TrainingTypeDAO;
import com.example.project_gym.service.TraineeService;
import com.example.project_gym.service.TrainerService;
import com.example.project_gym.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class AuthenticatedProfileConsoleCommandService {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainingTypeDAO trainingTypeDao;

    @Autowired
    private TrainingService trainingService;


    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ROOT);

    public AuthenticatedProfileConsoleCommandService() {
        dateFormat.setLenient(false);
    }

    public boolean isSelectTrainerCommand(String command) {
        return command.startsWith("trainer select ");
    }

    public boolean isSelectTraineeCommand(String command) {
        return command.startsWith("trainee select ");
    }

    public boolean isTraineePasswordChangeCommand(String command) {
        return command.startsWith("trainee password-change ");
    }

    public boolean isTrainerPasswordChangeCommand(String command) {
        return command.startsWith("trainer password-change ");
    }

    public boolean isUpdateTrainerCommand(String command) {
        return command.startsWith("trainer update ");
    }

    public boolean isUpdateTraineeCommand(String command) {
        return command.startsWith("trainee update ");
    }

    public boolean isActivateTraineeCommand(String command) {
        return command.startsWith("trainee activate ");
    }

    public boolean isDeactivateTraineeCommand(String command) {
        return command.startsWith("trainee deactivate ");
    }

    public boolean isActivateTrainerCommand(String command) {
        return command.startsWith("trainer activate ");
    }

    public boolean isDeactivateTrainerCommand(String command) {
        return command.startsWith("trainer deactivate ");
    }

    public boolean isDeleteTraineeCommand(String command) {
        return command.startsWith("trainee delete ");
    }

    public boolean isGetTraineeTrainingsCommand(String command) {
        return command.startsWith("trainee trainings ");
    }

    public boolean isGetTrainerTrainingsCommand(String command) {
        return command.startsWith("trainer trainings ");
    }

    public boolean isAddTrainingCommand(String command) {
        return command.startsWith("training add ");
    }

    public boolean isGetUnassignedTrainersCommand(String command) {
        return command.startsWith("trainee unassigned-trainers ");
    }

    public boolean isUpdateTraineesTrainersCommand(String command) {
        return command.startsWith("trainee trainers-update ");
    }

    public boolean isExitCommand(String command) {
        return "exit".equals(command);
    }

    public TrainerEntity selectTrainer(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainer select <username>");
        }
        String username = parts[2];
        return trainerService.selectByUsername(username);
    }

    public TraineeEntity selectTrainee(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainee select <username>");
        }
        String username = parts[2];
        return traineeService.selectByUsername(username);
    }

    public void changeTraineePassword(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 5) {
            throw new IllegalArgumentException("trainee password-change <username> <oldPassword> <newPassword>");
        }
        String username = parts[2];
        String oldPassword = parts[3];
        String newPassword = parts[4];

        PasswordChangeRequest dto = new PasswordChangeRequest(username, oldPassword, newPassword);
        traineeService.changePassword(dto);
    }

    public void changeTrainerPassword(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 5) {
            throw new IllegalArgumentException("trainer password-change <username> <oldPassword> <newPassword>");
        }
        String username = parts[2];
        String oldPassword = parts[3];
        String newPassword = parts[4];

        PasswordChangeRequest dto = new PasswordChangeRequest(username, oldPassword, newPassword);
        trainerService.changePassword(dto);
    }

    public TraineeEntity updateTrainee(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainee update <username> [address=<value>] [active=true|false] [dateOfBirth=dd-MM-yyyy]");
        }

        String username = parts[2];
        TraineeEntity traineeEntity = traineeService.selectByUsername(username);

        String address = traineeEntity.getAddress();
        Boolean active = null;
        LocalDateTime dateOfBirth = traineeEntity.getDateOfBirth();

        for (int i = 3; i < parts.length; i++) {
            String token = parts[i];
            if (token.startsWith("address=")) {
                address = parseOptionalString(token.substring("address=".length()));
            } else if (token.startsWith("active=")) {
                active = parseBooleanStrict(token.substring("active=".length()));
            } else if (token.startsWith("dateOfBirth=")) {
                dateOfBirth = parseOptionalDate(token.substring("dateOfBirth=".length()), "dateOfBirth");
            }
        }

        UpdateTraineeRequest dto = new UpdateTraineeRequest(active, address, dateOfBirth);
        return traineeService.update(traineeEntity.getId(), dto);
    }

    public TrainerEntity updateTrainer(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainer update <username> [specialization=<type>] [active=true|false]");
        }

        String username = parts[2];
        TrainerEntity trainerEntity = trainerService.selectByUsername(username);

        String specialization = null;
        Boolean active = null;

        for (int i = 3; i < parts.length; i++) {
            String token = parts[i];
            if (token.startsWith("specialization=")) {
                String trainingTypeName = token.substring("specialization=".length());
                specialization = trainingTypeDao.findByTrainingTypeName(trainingTypeName)
                        .map(TrainingType::getTrainingTypeName)
                        .orElseThrow(() -> new IllegalArgumentException("Training type not found: " + trainingTypeName));
            } else if (token.startsWith("active=")) {
                active = parseBooleanStrict(token.substring("active=".length()));
            }
        }

        UpdateTrainerRequest dto = new UpdateTrainerRequest(active, specialization);
        return trainerService.update(trainerEntity.getId(), dto);
    }

    public TraineeEntity activateTrainee(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainee activate <username>");
        }
        String username = parts[2];
        TraineeEntity traineeEntity = traineeService.selectByUsername(username);

        if (traineeEntity.getUser().isActive()) {
            throw new IllegalStateException("Trainee is already active");
        }

        UpdateTraineeRequest dto = new UpdateTraineeRequest(true, null, null);
        return traineeService.update(traineeEntity.getId(), dto);
    }

    public TraineeEntity deactivateTrainee(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainee deactivate <username>");
        }
        String username = parts[2];
        TraineeEntity traineeEntity = traineeService.selectByUsername(username);

        if (!traineeEntity.getUser().isActive()) {
            throw new IllegalStateException("Trainee is already inactive");
        }

        UpdateTraineeRequest dto = new UpdateTraineeRequest(false, null, null);
        return traineeService.update(traineeEntity.getId(), dto);
    }

    public TrainerEntity activateTrainer(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainer activate <username>");
        }
        String username = parts[2];
        TrainerEntity trainerEntity = trainerService.selectByUsername(username);

        if (trainerEntity.getUser().isActive()) {
            throw new IllegalStateException("Trainer is already active");
        }

        UpdateTrainerRequest dto = new UpdateTrainerRequest(true, trainerEntity.getTrainingType().getTrainingTypeName());
        return trainerService.update(trainerEntity.getId(), dto);
    }

    public TrainerEntity deactivateTrainer(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainer deactivate <username>");
        }
        String username = parts[2];
        TrainerEntity trainerEntity = trainerService.selectByUsername(username);

        if (!trainerEntity.getUser().isActive()) {
            throw new IllegalStateException("Trainer is already inactive");
        }

        UpdateTrainerRequest dto = new UpdateTrainerRequest(false, trainerEntity.getTrainingType().getTrainingTypeName());
        return trainerService.update(trainerEntity.getId(), dto);
    }

    public boolean deleteTrainee(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainee delete <username>");
        }
        String username = parts[2];
        return traineeService.deleteByUsername(username);
    }

    public List<TrainingEntity> getTraineeTrainings(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainee trainings <username> [fromDate=dd-MM-yyyy] [toDate=dd-MM-yyyy] [trainerName=<name>] [trainingType=<type>]");
        }

        String traineeUsername = parts[2];
        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;
        String trainerName = null;
        String trainingType = null;

        for (int i = 3; i < parts.length; i++) {
            String token = parts[i];
            if (token.startsWith("fromDate=")) {
                try {
                    fromDate = toLocalDateTime(dateFormat.parse(token.substring("fromDate=".length())));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid date format for fromDate. Use dd-MM-yyyy");
                }
            } else if (token.startsWith("toDate=")) {
                try {
                    toDate = toLocalDateTime(dateFormat.parse(token.substring("toDate=".length())));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid date format for toDate. Use dd-MM-yyyy");
                }
            } else if (token.startsWith("trainerName=")) {
                trainerName = token.substring("trainerName=".length());
            } else if (token.startsWith("trainingType=")) {
                trainingType = token.substring("trainingType=".length());
            }
        }

        TraineeTrainingsFilterRequest dto = new TraineeTrainingsFilterRequest(traineeUsername, fromDate, toDate, trainerName, trainingType);
        return traineeService.getTrainings(dto);
    }

    public List<TrainingEntity> getTrainerTrainings(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainer trainings <username> [fromDate=dd-MM-yyyy] [toDate=dd-MM-yyyy] [traineeName=<name>]");
        }

        String trainerUsername = parts[2];
        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;
        String traineeName = null;

        for (int i = 3; i < parts.length; i++) {
            String token = parts[i];
            if (token.startsWith("fromDate=")) {
                try {
                    fromDate = toLocalDateTime(dateFormat.parse(token.substring("fromDate=".length())));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid date format for fromDate. Use dd-MM-yyyy");
                }
            } else if (token.startsWith("toDate=")) {
                try {
                    toDate = toLocalDateTime(dateFormat.parse(token.substring("toDate=".length())));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid date format for toDate. Use dd-MM-yyyy");
                }
            } else if (token.startsWith("traineeName=")) {
                traineeName = token.substring("traineeName=".length());
            }
        }

        TrainerTrainingsFilterRequest dto = new TrainerTrainingsFilterRequest(trainerUsername, fromDate, toDate, traineeName);
        return trainerService.getTrainings(dto);
    }

    public TrainingEntity addTraining(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 8) {
            throw new IllegalArgumentException("training add <traineeUsername> <trainerUsername> <trainingTypeName> <trainingName> <dd-MM-yyyy> <durationMinutes>");
        }

        String traineeUsername = parts[2];
        String trainerUsername = parts[3];
        String trainingTypeName = parts[4];
        String trainingName = parts[5];
        LocalDateTime trainingDate;
        long trainingDuration;

        try {
            trainingDate = toLocalDateTime(dateFormat.parse(parts[6]));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Use dd-MM-yyyy");
        }

        try {
            trainingDuration = Long.parseLong(parts[7]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Training duration must be a number");
        }

        if (trainingDuration <= 0) {
            throw new IllegalArgumentException("Training duration must be greater than 0");
        }

        TraineeEntity traineeEntity = traineeService.selectByUsername(traineeUsername);
        TrainerEntity trainerEntity = trainerService.selectByUsername(trainerUsername);
        TrainingType trainingType = trainingTypeDao.findByTrainingTypeName(trainingTypeName)
                .orElseThrow(() -> new IllegalArgumentException("Training type not found: " + trainingTypeName));

        CreateTrainingRequest dto = new CreateTrainingRequest(trainerEntity.getId(), traineeEntity.getId(), trainingName,
                trainingType, trainingDate, trainingDuration);

        return trainingService.create(dto);
    }

    public List<TrainerEntity> getUnassignedTrainers(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainee unassigned-trainers <username>");
        }
        String traineeUsername = parts[2];
        return traineeService.getUnassignedTrainers(traineeUsername);
    }

    public void updateTraineesTrainers(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 4) {
            throw new IllegalArgumentException("trainee trainers-update <username> <trainerUsername1,trainerUsername2,...>");
        }

        String traineeUsername = parts[2];
        List<TrainerEntity> trainerEntities = new ArrayList<>();

        for (int i = 3; i < parts.length; i++) {
            String[] trainerUsernames = parts[i].split(",");
            for (String trainerUsername : trainerUsernames) {
                String username = trainerUsername.trim();
                if (!username.isEmpty()) {
                    trainerEntities.add(trainerService.selectByUsername(username));
                }
            }
        }

        if (trainerEntities.isEmpty()) {
            throw new IllegalArgumentException("At least one trainer username is required");
        }

        traineeService.updateTrainersList(traineeUsername, trainerEntities);
    }

    private Boolean parseBooleanStrict(String rawValue) {
        if ("true".equalsIgnoreCase(rawValue)) {
            return true;
        }
        if ("false".equalsIgnoreCase(rawValue)) {
            return false;
        }
        throw new IllegalArgumentException("Invalid value for active. Use true or false.");
    }

    private String parseOptionalString(String rawValue) {
        if (rawValue == null) {
            return null;
        }
        if (rawValue.isBlank() || "null".equalsIgnoreCase(rawValue) || "-".equals(rawValue)) {
            return null;
        }
        return rawValue;
    }

    private LocalDateTime parseOptionalDate(String rawValue, String fieldName) {
        if (rawValue == null || rawValue.isBlank() || "null".equalsIgnoreCase(rawValue) || "-".equals(rawValue)) {
            return null;
        }
        try {
            return toLocalDateTime(dateFormat.parse(rawValue));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format for " + fieldName + ". Use dd-MM-yyyy");
        }
    }

    private LocalDateTime toLocalDateTime(java.util.Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
