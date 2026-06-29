package com.example.project_gym.utilservices;
import com.example.project_gym.model.request.create.TrainerCreateRequest;
import com.example.project_gym.model.request.create.TraineeCreateRequest;
import com.example.project_gym.utilservices.guestservices.ProfileCreationParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ProfileCreationParserServiceTest {
    private ProfileCreationParserService service;
    @BeforeEach
    void setUp() {
        service = new ProfileCreationParserService();
    }
    @Test
    void parseTrainerCreate_shouldParseValidInput() {
        String input = "trainer create John Doe CARDIO";
        TrainerCreateRequest request = service.parseTrainerCreate(input);
        assertEquals("John", request.firstName());
        assertEquals("Doe", request.lastName());
        assertEquals("CARDIO", request.specialization());
    }
    @Test
    void parseTrainerCreate_shouldThrowWhenInsufficientArgs() {
        String input = "trainer create John";
        assertThrows(IllegalArgumentException.class, () -> service.parseTrainerCreate(input));
    }

    @Test
    void parseTrainerCreate_shouldThrowWhenBlankArgs() {
        String input = "trainer create  Doe CARDIO";
        assertThrows(IllegalArgumentException.class, () -> service.parseTrainerCreate(input));
    }

    @Test
    void parseTraineeCreate_shouldParseValidInput() {
        String input = "trainee create Jane Smith";
        TraineeCreateRequest request = service.parseTraineeCreate(input);
        assertEquals("Jane", request.firstName());
        assertEquals("Smith", request.lastName());
        assertNull(request.dateOfBirth());
        assertNull(request.address());
    }
    @Test
    void parseTraineeCreate_shouldThrowWhenInsufficientArgs() {
        String input = "trainee create Jane";
        assertThrows(IllegalArgumentException.class, () -> service.parseTraineeCreate(input));
    }

    @Test
    void parseTraineeCreate_shouldParseWithAddress() {
        String input = "trainee create Jane Smith 123 Main Street";
        TraineeCreateRequest request = service.parseTraineeCreate(input);
        assertEquals("Jane", request.firstName());
        assertEquals("Smith", request.lastName());
        assertEquals("123 Main Street", request.address());
        assertNull(request.dateOfBirth());
    }

    @Test
    void parseTraineeCreate_shouldParseWithDate() {
        String input = "trainee create Jane Smith 15-03-1990";
        TraineeCreateRequest request = service.parseTraineeCreate(input);
        assertEquals("Jane", request.firstName());
        assertEquals("Smith", request.lastName());
        assertNotNull(request.dateOfBirth());
        assertNull(request.address());
    }

    @Test
    void parseTraineeCreate_shouldParseWithDateAndAddress() {
        String input = "trainee create Jane Smith 15-03-1990 123 Main Street";
        TraineeCreateRequest request = service.parseTraineeCreate(input);
        assertEquals("Jane", request.firstName());
        assertEquals("Smith", request.lastName());
        assertNotNull(request.dateOfBirth());
        assertEquals("123 Main Street", request.address());
    }

    @Test
    void parseTraineeCreate_shouldThrowWhenBlankArgs() {
        String input = "trainee create  Smith";
        assertThrows(IllegalArgumentException.class, () -> service.parseTraineeCreate(input));
    }
}