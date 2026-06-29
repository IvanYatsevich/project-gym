package com.example.project_gym.utilservices;
import com.example.project_gym.utilservices.guestservices.password.PasswordGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PasswordGeneratorTest {
    private PasswordGenerator generator;
    @BeforeEach
    void setUp() {
        generator = new PasswordGenerator();
    }
    @Test
    void generatePassword_shouldGeneratePassword() {
        String password = generator.generatePassword();
        assertNotNull(password);
        assertEquals(10, password.length());
    }
    @Test
    void generatePassword_shouldGenerateDifferentPasswords() {
        String password1 = generator.generatePassword();
        String password2 = generator.generatePassword();
        assertNotEquals(password1, password2);
    }

    @Test
    void generatePassword_shouldOnlyContainValidCharacters() {
        String password = generator.generatePassword();
        assertTrue(password.matches("[A-Za-z0-9]+"));
    }

    @Test
    void generatePassword_shouldGenerateMultipleUniquePasswords() {
        for (int i = 0; i < 5; i++) {
            String password = generator.generatePassword();
            assertEquals(10, password.length());
            assertTrue(password.matches("[A-Za-z0-9]+"));
        }
    }
}