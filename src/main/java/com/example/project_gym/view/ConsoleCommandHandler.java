package com.example.project_gym.view;

import com.example.project_gym.model.TrainingType;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;

@Component
public class ConsoleCommandHandler {

    private static final String HELP = """
            Commands:
              help
              exit
              trainer create <firstName> <lastName> <CARDIO|STRENGTH>
              trainer update <id> <true|false|null> <CARDIO|STRENGTH|null>
              trainer get <id>
              trainee create <firstName> <lastName> <yyyy-MM-dd> <address>
              trainee update <id> <true|false|null> <address|->
              trainee delete <id>
              trainee get <id>
              training create <trainerId> <traineeId> <trainingName> <CARDIO|STRENGTH> <yyyy-MM-dd> <durationMinutes>
              training get <id>
            """;

    private final GymFacade gymFacade;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public ConsoleCommandHandler(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    public String handle(String input) {
        String line = input == null ? "" : input.trim();
        if (line.isEmpty()) {
            return "";
        }

        String[] tokens = line.split("\\s+");

        try {
            if ("help".equalsIgnoreCase(tokens[0])) {
                return HELP;
            }
            if ("exit".equalsIgnoreCase(tokens[0])) {
                return "exit";
            }

            if ("trainer".equalsIgnoreCase(tokens[0])) {
                return handleTrainer(tokens);
            }
            if ("trainee".equalsIgnoreCase(tokens[0])) {
                return handleTrainee(tokens);
            }
            if ("training".equalsIgnoreCase(tokens[0])) {
                return handleTraining(tokens);
            }

            return "Unknown command. Type 'help'.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String handleTrainer(String[] tokens) {
        requireMin(tokens, 3);

        if ("create".equalsIgnoreCase(tokens[1])) {
            requireMin(tokens, 5);
            var trainer = gymFacade.createTrainer(
                    tokens[2],
                    tokens[3],
                    TrainingType.valueOf(tokens[4].toUpperCase())
            );
            return "Created trainer: " + trainer;
        }

        if ("update".equalsIgnoreCase(tokens[1])) {
            requireMin(tokens, 5);
            Long id = Long.parseLong(tokens[2]);
            Boolean isActive = parseNullableBoolean(tokens[3]);
            TrainingType specialization = parseNullableTrainingType(tokens[4]);
            var trainer = gymFacade.updateTrainer(id, isActive, specialization);
            return "Updated trainer: " + trainer;
        }

        if ("get".equalsIgnoreCase(tokens[1])) {
            var trainer = gymFacade.getTrainer(Long.parseLong(tokens[2]));
            return "Trainer: " + trainer;
        }

        return "Unknown trainer action. Type 'help'.";
    }

    private String handleTrainee(String[] tokens) throws ParseException {
        requireMin(tokens, 3);

        if ("create".equalsIgnoreCase(tokens[1])) {
            requireMin(tokens, 6);
            Date date = dateFormat.parse(tokens[4]);
            String address = String.join(" ", Arrays.copyOfRange(tokens, 5, tokens.length));
            var trainee = gymFacade.createTrainee(tokens[2], tokens[3], date, address);
            return "Created trainee: " + trainee;
        }

        if ("update".equalsIgnoreCase(tokens[1])) {
            requireMin(tokens, 5);
            Long id = Long.parseLong(tokens[2]);
            Boolean isActive = parseNullableBoolean(tokens[3]);
            String address = String.join(" ", Arrays.copyOfRange(tokens, 4, tokens.length));
            if ("-".equals(address)) {
                address = null;
            }
            var trainee = gymFacade.updateTrainee(id, isActive, address);
            return "Updated trainee: " + trainee;
        }

        if ("delete".equalsIgnoreCase(tokens[1])) {
            requireMin(tokens, 3);
            boolean deleted = gymFacade.deleteTrainee(Long.parseLong(tokens[2]));
            return deleted ? "Trainee deleted." : "Trainee was not deleted.";
        }

        if ("get".equalsIgnoreCase(tokens[1])) {
            var trainee = gymFacade.getTrainee(Long.parseLong(tokens[2]));
            return "Trainee: " + trainee;
        }

        return "Unknown trainee action. Type 'help'.";
    }

    private String handleTraining(String[] tokens) throws ParseException {
        requireMin(tokens, 3);

        if ("create".equalsIgnoreCase(tokens[1])) {
            requireMin(tokens, 8);

            Long trainerId = Long.parseLong(tokens[2]);
            Long traineeId = Long.parseLong(tokens[3]);
            String trainingName = tokens[4];
            TrainingType trainingType = TrainingType.valueOf(tokens[5].toUpperCase());
            Date trainingDate = dateFormat.parse(tokens[6]);
            Duration duration = Duration.ofMinutes(Long.parseLong(tokens[7]));

            var training = gymFacade.createTraining(
                    trainerId,
                    traineeId,
                    trainingName,
                    trainingType,
                    trainingDate,
                    duration
            );
            return "Created training: " + training;
        }

        if ("get".equalsIgnoreCase(tokens[1])) {
            var training = gymFacade.getTraining(Long.parseLong(tokens[2]));
            return "Training: " + training;
        }

        return "Unknown training action. Type 'help'.";
    }

    private TrainingType parseNullableTrainingType(String token) {
        if ("null".equalsIgnoreCase(token)) {
            return null;
        }
        return TrainingType.valueOf(token.toUpperCase());
    }

    private Boolean parseNullableBoolean(String token) {
        if ("null".equalsIgnoreCase(token)) {
            return null;
        }
        return Boolean.parseBoolean(token);
    }

    private void requireMin(String[] tokens, int expectedMin) {
        if (tokens.length < expectedMin) {
            throw new IllegalArgumentException("Not enough arguments. Type 'help'.");
        }
    }
}
