package com.example.project_gym.utilservices.guestservices.password;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
@Component
public class PasswordGenerator {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String generatePassword(){
       SecureRandom random = new SecureRandom();
       StringBuilder password = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            password.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return password.toString();
    }
}
