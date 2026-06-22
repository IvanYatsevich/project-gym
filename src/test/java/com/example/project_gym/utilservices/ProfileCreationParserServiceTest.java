package com.example.project_gym.utilservices;

import com.example.project_gym.model.dto.dtoin.TraineeDtoIn;
import com.example.project_gym.model.dto.dtoin.TrainerDtoIn;
import com.example.project_gym.utilservices.unauthservices.ProfileCreationParserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileCreationParserServiceTest {

    private final ProfileCreationParserService parser = new ProfileCreationParserService();

    @Test
    void parseTrainerCreate_shouldParseValues() {
        TrainerDtoIn dto = parser.parseTrainerCreate("trainer create Ivan Ivanov CARDIO");

        assertEquals("Ivan", dto.firstName());
        assertEquals("Ivanov", dto.lastName());
        assertEquals("CARDIO", dto.specialization());
    }

    @Test
    void parseTraineeCreate_shouldParseDateAndAddress() {
        TraineeDtoIn dto = parser.parseTraineeCreate("trainee create Hulk Hogan 21-04-1995 New York");

        assertEquals("Hulk", dto.firstName());
        assertEquals("Hogan", dto.lastName());
        assertNotNull(dto.dateOfBirth());
        assertEquals("New York", dto.address());
    }

    @Test
    void parseTraineeCreate_shouldThrowOnMissingNames() {
        assertThrows(IllegalArgumentException.class, () -> parser.parseTraineeCreate("trainee create Hulk"));
    }
}

