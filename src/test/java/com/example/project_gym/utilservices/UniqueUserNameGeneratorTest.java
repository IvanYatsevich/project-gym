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
    private RawUserNameGenerator rawUserNameGenerator;
    @Mock
    private UsernameLookupService lookupService;

    private UniqueUserNameGenerator uniqueUserNameGenerator;

    @BeforeEach
    void setUp() {
        uniqueUserNameGenerator = new UniqueUserNameGenerator();
        uniqueUserNameGenerator.setUserNameGenerator(rawUserNameGenerator);
        uniqueUserNameGenerator.setUserLookupService(lookupService);
    }

    @Test
    void generateUnique_shouldReturnBaseNameWhenFree() {
        when(rawUserNameGenerator.generateUserName("Ivan", "Ivanov")).thenReturn("ivan.ivanov");
        when(lookupService.existsByUserName("ivan.ivanov")).thenReturn(false);

        String result = uniqueUserNameGenerator.generateUnique("Ivan", "Ivanov");

        assertEquals("ivan.ivanov", result);
    }

    @Test
    void generateUnique_shouldReturnNameWithSuffixWhenBaseExists() {
        when(rawUserNameGenerator.generateUserName("Ivan", "Ivanov")).thenReturn("ivan.ivanov");
        when(lookupService.existsByUserName("ivan.ivanov")).thenReturn(true);
        when(lookupService.existsByUserName("ivan.ivanov1")).thenReturn(true);
        when(lookupService.existsByUserName("ivan.ivanov2")).thenReturn(false);

        String result = uniqueUserNameGenerator.generateUnique("Ivan", "Ivanov");

        assertEquals("ivan.ivanov2", result);
    }
}

