package com.example.project_gym.model.request;


public record CreateTrainerRequest(String firstName,
                                   String lastName,
                                   String specialization) {
}
