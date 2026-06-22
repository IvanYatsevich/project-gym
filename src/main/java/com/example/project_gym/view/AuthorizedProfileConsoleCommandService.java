package com.example.project_gym.view;

import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Training;
import com.example.project_gym.model.TrainingType;
import com.example.project_gym.model.dto.dtoin.PasswordChangeDto;
import com.example.project_gym.model.dto.dtoin.TrainingDtoIn;
import com.example.project_gym.model.dto.dtoin.TrainingFilterDto;
import com.example.project_gym.model.dto.dtoin.TrainerTrainingsFilterDto;
import com.example.project_gym.model.dto.dtoupdate.TraineeUpdateDto;
import com.example.project_gym.model.dto.dtoupdate.TrainerUpdateDto;
import com.example.project_gym.repository.idao.ITrainingTypeDAO;
import com.example.project_gym.service.TraineeService;
import com.example.project_gym.service.TrainerService;
import com.example.project_gym.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Component
public class AuthorizedProfileConsoleCommandService {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private ITrainingTypeDAO trainingTypeDao;

    @Autowired
    private TrainingService trainingService;


    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy", Locale.ROOT);

    public AuthorizedProfileConsoleCommandService() {
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

    public Trainer selectTrainer(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainer select <username>");
        }
        String username = parts[2];
        return trainerService.selectByUsername(username);
    }

    public Trainee selectTrainee(String command) {
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

        PasswordChangeDto dto = new PasswordChangeDto(username, oldPassword, newPassword);
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

        PasswordChangeDto dto = new PasswordChangeDto(username, oldPassword, newPassword);
        trainerService.changePassword(dto);
    }

    public Trainee updateTrainee(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainee update <username> [address=<value>] [active=true|false] [dateOfBirth=dd-MM-yyy]");
        }

        String username = parts[2];
        Trainee trainee = traineeService.selectByUsername(username);

        String address = trainee.getAddress();
        Boolean active = null;
        Date dateOfBirth = trainee.getDateOfBirth();

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

        TraineeUpdateDto dto = new TraineeUpdateDto(active, address, dateOfBirth);
        return traineeService.update(trainee.getId(), dto);
    }

    public Trainer updateTrainer(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainer update <username> [specialization=<type>] [active=true|false]");
        }

        String username = parts[2];
        Trainer trainer = trainerService.selectByUsername(username);

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

        TrainerUpdateDto dto = new TrainerUpdateDto(active, specialization);
        return trainerService.update(trainer.getId(), dto);
    }

    public Trainee activateTrainee(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainee activate <username>");
        }
        String username = parts[2];
        Trainee trainee = traineeService.selectByUsername(username);

        if (trainee.getUser().isActive()) {
            throw new IllegalStateException("Trainee is already active");
        }

        TraineeUpdateDto dto = new TraineeUpdateDto(true, null, null);
        return traineeService.update(trainee.getId(), dto);
    }

    public Trainee deactivateTrainee(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainee deactivate <username>");
        }
        String username = parts[2];
        Trainee trainee = traineeService.selectByUsername(username);

        if (!trainee.getUser().isActive()) {
            throw new IllegalStateException("Trainee is already inactive");
        }

        TraineeUpdateDto dto = new TraineeUpdateDto(false, null, null);
        return traineeService.update(trainee.getId(), dto);
    }

    public Trainer activateTrainer(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainer activate <username>");
        }
        String username = parts[2];
        Trainer trainer = trainerService.selectByUsername(username);

        if (trainer.getUser().isActive()) {
            throw new IllegalStateException("Trainer is already active");
        }

        TrainerUpdateDto dto = new TrainerUpdateDto(true, trainer.getTrainingType().getTrainingTypeName());
        return trainerService.update(trainer.getId(), dto);
    }

    public Trainer deactivateTrainer(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainer deactivate <username>");
        }
        String username = parts[2];
        Trainer trainer = trainerService.selectByUsername(username);

        if (!trainer.getUser().isActive()) {
            throw new IllegalStateException("Trainer is already inactive");
        }

        TrainerUpdateDto dto = new TrainerUpdateDto(false, trainer.getTrainingType().getTrainingTypeName());
        return trainerService.update(trainer.getId(), dto);
    }

    public boolean deleteTrainee(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainee delete <username>");
        }
        String username = parts[2];
        return traineeService.deleteByUsername(username);
    }

    public List<Training> getTraineeTrainings(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainee trainings <username> [fromDate=dd-MM-yyy] [toDate=dd-MM-yyy] [trainerName=<name>] [trainingType=<type>]");
        }

        String traineeUsername = parts[2];
        Date fromDate = null;
        Date toDate = null;
        String trainerName = null;
        String trainingType = null;

        for (int i = 3; i < parts.length; i++) {
            String token = parts[i];
            if (token.startsWith("fromDate=")) {
                try {
                    fromDate = dateFormat.parse(token.substring("fromDate=".length()));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid date format for fromDate. Use dd-MM-yyy");
                }
            } else if (token.startsWith("toDate=")) {
                try {
                    toDate = dateFormat.parse(token.substring("toDate=".length()));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid date format for toDate. Use dd-MM-yyy");
                }
            } else if (token.startsWith("trainerName=")) {
                trainerName = token.substring("trainerName=".length());
            } else if (token.startsWith("trainingType=")) {
                trainingType = token.substring("trainingType=".length());
            }
        }

        TrainingFilterDto dto = new TrainingFilterDto(traineeUsername, fromDate, toDate, trainerName, trainingType);
        return traineeService.getTrainings(dto);
    }

    public List<Training> getTrainerTrainings(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("trainer trainings <username> [fromDate=dd-MM-yyy] [toDate=dd-MM-yyy] [traineeName=<name>]");
        }

        String trainerUsername = parts[2];
        Date fromDate = null;
        Date toDate = null;
        String traineeName = null;

        for (int i = 3; i < parts.length; i++) {
            String token = parts[i];
            if (token.startsWith("fromDate=")) {
                try {
                    fromDate = dateFormat.parse(token.substring("fromDate=".length()));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid date format for fromDate. Use dd-MM-yyy");
                }
            } else if (token.startsWith("toDate=")) {
                try {
                    toDate = dateFormat.parse(token.substring("toDate=".length()));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid date format for toDate. Use dd-MM-yyy");
                }
            } else if (token.startsWith("traineeName=")) {
                traineeName = token.substring("traineeName=".length());
            }
        }

        TrainerTrainingsFilterDto dto = new TrainerTrainingsFilterDto(trainerUsername, fromDate, toDate, traineeName);
        return trainerService.getTrainings(dto);
    }

    public Training addTraining(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 8) {
            throw new IllegalArgumentException("training add <traineeUsername> <trainerUsername> <trainingTypeName> <trainingName> <dd-MM-yyy> <durationMinutes>");
        }

        String traineeUsername = parts[2];
        String trainerUsername = parts[3];
        String trainingTypeName = parts[4];
        String trainingName = parts[5];
        Date trainingDate;
        long trainingDuration;

        try {
            trainingDate = dateFormat.parse(parts[6]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Use dd-MM-yyy");
        }

        try {
            trainingDuration = Long.parseLong(parts[7]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Training duration must be a number");
        }

        if (trainingDuration <= 0) {
            throw new IllegalArgumentException("Training duration must be greater than 0");
        }

        Trainee trainee = traineeService.selectByUsername(traineeUsername);
        Trainer trainer = trainerService.selectByUsername(trainerUsername);
        TrainingType trainingType = trainingTypeDao.findByTrainingTypeName(trainingTypeName)
                .orElseThrow(() -> new IllegalArgumentException("Training type not found: " + trainingTypeName));

        TrainingDtoIn dto = new TrainingDtoIn(trainer.getId(), trainee.getId(), trainingName,
                trainingType, trainingDate, trainingDuration);

        return trainingService.create(dto);
    }

    public List<Trainer> getUnassignedTrainers(String command) {
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
        List<Trainer> trainers = new ArrayList<>();

        for (int i = 3; i < parts.length; i++) {
            String[] trainerUsernames = parts[i].split(",");
            for (String trainerUsername : trainerUsernames) {
                String username = trainerUsername.trim();
                if (!username.isEmpty()) {
                    trainers.add(trainerService.selectByUsername(username));
                }
            }
        }

        if (trainers.isEmpty()) {
            throw new IllegalArgumentException("At least one trainer username is required");
        }

        traineeService.updateTrainersList(traineeUsername, trainers);
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

    private Date parseOptionalDate(String rawValue, String fieldName) {
        if (rawValue == null || rawValue.isBlank() || "null".equalsIgnoreCase(rawValue) || "-".equals(rawValue)) {
            return null;
        }
        try {
            return dateFormat.parse(rawValue);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format for " + fieldName + ". Use dd-MM-yyy");
        }
    }
}





