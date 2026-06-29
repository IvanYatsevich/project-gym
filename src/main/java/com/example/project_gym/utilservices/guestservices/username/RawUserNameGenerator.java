package com.example.project_gym.utilservices.guestservices.username;

import org.springframework.stereotype.Service;

@Service
public class RawUserNameGenerator {
    public String generateUserName(String firstName, String lastName) {
        return firstName + "." + lastName;
    }
}
