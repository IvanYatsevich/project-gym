package com.example.project_gym.model.dto.dtoin;

import java.util.Date;

public record TraineeDtoIn(String firstName,
                           String lastName,
                           Date dateOfBirth,
                           String address) {
}
