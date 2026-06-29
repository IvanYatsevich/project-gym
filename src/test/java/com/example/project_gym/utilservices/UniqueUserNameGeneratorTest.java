package com.example.project_gym.utilservices;
import com.example.project_gym.utilservices.guestservices.username.RawUserNameGenerator;
import com.example.project_gym.utilservices.guestservices.username.UniqueUserNameGenerator;
import com.example.project_gym.utilservices.guestservices.username.UsernameLookupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class UniqueUserNameGeneratorTest {
    @Mock
    private UsernameLookupService usernameLookupService;
    @Mock
    private RawUserNameGenerator rawUserNameGenerator;
    private UniqueUserNameGenerator generator;
    @BeforeEach
    void setUp() {
        generator = new UniqueUserNameGenerator();
        generator.setUserLookupService(usernameLookupService);
        generator.setUserNameGenerator(rawUserNameGenerator);
    }
    @Test
    void generateUnique_shouldReturnBaseUsernameWhenNotExists() {
        when(rawUserNameGenerator.generateUserName("John", "Doe")).thenReturn("John.Doe");
        when(usernameLookupService.existsByUserName("John.Doe")).thenReturn(false);
        String result = generator.generateUnique("John", "Doe");
        assertEquals("John.Doe", result);
    }
    @Test
    void generateUnique_shouldAppendSuffixWhenExists() {
        when(rawUserNameGenerator.generateUserName("John", "Doe")).thenReturn("John.Doe");
        when(usernameLookupService.existsByUserName("John.Doe")).thenReturn(true);
        when(usernameLookupService.existsByUserName("John.Doe1")).thenReturn(false);
        String result = generator.generateUnique("John", "Doe");
        assertEquals("John.Doe1", result);
    }

    @Test
    void generateUnique_shouldAppendMultipleSuffixesWhenNeeded() {
        when(rawUserNameGenerator.generateUserName("Jane", "Smith")).thenReturn("Jane.Smith");
        when(usernameLookupService.existsByUserName("Jane.Smith")).thenReturn(true);
        when(usernameLookupService.existsByUserName("Jane.Smith1")).thenReturn(true);
        when(usernameLookupService.existsByUserName("Jane.Smith2")).thenReturn(false);
        String result = generator.generateUnique("Jane", "Smith");
        assertEquals("Jane.Smith2", result);
    }
}