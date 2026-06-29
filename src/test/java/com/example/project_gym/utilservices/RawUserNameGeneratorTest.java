package com.example.project_gym.utilservices;
import com.example.project_gym.utilservices.guestservices.username.RawUserNameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
class RawUserNameGeneratorTest {
    private RawUserNameGenerator generator;
    @BeforeEach
    void setUp() {
        generator = new RawUserNameGenerator();
    }
    @Test
    void generateUserName_shouldFormatCorrectly() {
        String username = generator.generateUserName("John", "Doe");
        assertEquals("John.Doe", username);
    }

    @Test
    void generateUserName_shouldHandleDifferentNames() {
        String username = generator.generateUserName("Jane", "Smith");
        assertEquals("Jane.Smith", username);
    }

    @Test
    void generateUserName_shouldHandleSingleCharNames() {
        String username = generator.generateUserName("J", "S");
        assertEquals("J.S", username);
    }

    @Test
    void generateUserName_shouldHandleLongNames() {
        String username = generator.generateUserName("Christopher", "Alexander");
        assertEquals("Christopher.Alexander", username);
    }
}