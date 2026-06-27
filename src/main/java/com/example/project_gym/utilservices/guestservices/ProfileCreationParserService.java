package com.example.project_gym.utilservices.guestservices;

import com.example.project_gym.model.request.CreateTraineeRequest;
import com.example.project_gym.model.request.CreateTrainerRequest;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

@Component
public class ProfileCreationParserService {

    public CreateTrainerRequest parseTrainerCreate(String input) {
        String[] parts = input.split("\\s+");

        if (parts.length < 5) {
            throw new IllegalArgumentException("trainer create requires firstName, lastName and trainingTypeName.");
        }

        String firstName = parts[2];
        String lastName = parts[3];
        String trainingTypeName = parts[4];

        if (firstName.isBlank() || lastName.isBlank() || trainingTypeName.isBlank()) {
            throw new IllegalArgumentException("trainer create requires firstName, lastName and trainingTypeName.");
        }

        return new CreateTrainerRequest(firstName, lastName, trainingTypeName);
    }

    public CreateTraineeRequest parseTraineeCreate(String input) {
        String[] parts = input.split("\\s+");

        if (parts.length < 4) {
            throw new IllegalArgumentException("trainee create requires firstName and lastName.");
        }

        String firstName = parts[2];
        String lastName = parts[3];

        if (firstName.isBlank() || lastName.isBlank()) {
            throw new IllegalArgumentException("trainee create requires firstName and lastName.");
        }

        LocalDateTime dateOfBirth = null;
        String address = null;

        if (parts.length >= 5) {
            String fifthToken = parts[4];
            if (looksLikeDate(fifthToken)) {
                dateOfBirth = parseDate(fifthToken);
                if (parts.length > 5) {
                    address = String.join(" ", Arrays.copyOfRange(parts, 5, parts.length));
                }
            } else {
                address = String.join(" ", Arrays.copyOfRange(parts, 4, parts.length));
            }
        }

        return new CreateTraineeRequest(firstName, lastName, dateOfBirth, address);
    }

    private LocalDateTime parseDate(String value) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
        dateFormat.setLenient(false);

        try {
            return Instant.ofEpochMilli(dateFormat.parse(value).getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use dd-MM-yyy.");
        }
    }

    private boolean looksLikeDate(String value) {
        return value.matches("\\d{2}-\\d{2}-\\d{3,4}");
    }

}

