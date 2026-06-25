package com.example.project_gym.model.request;

import java.util.Date;

public record CreateTraineeRequest(String firstName,
                                   String lastName,
                                   Date dateOfBirth,
                                   String address) {
}
