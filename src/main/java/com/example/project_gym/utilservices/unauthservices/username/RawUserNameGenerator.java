package com.example.project_gym.utilservices.unauthservices.username;

import org.springframework.stereotype.Service;

@Service
public class RawUserNameGenerator {
    public String generateUserName(String firstName, String lastName) {
        return firstName + "." + lastName;
    }
}
