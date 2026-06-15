package com.example.project_gym.utilservices;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    @Test
    void generatePassword_shouldReturnTenCharAlphaNumeric() {
        PasswordGenerator generator = new PasswordGenerator();

        String password = generator.generatePassword();

        assertNotNull(password);
        assertEquals(10, password.length());
        assertTrue(password.matches("[A-Za-z0-9]{10}"));
    }
}

