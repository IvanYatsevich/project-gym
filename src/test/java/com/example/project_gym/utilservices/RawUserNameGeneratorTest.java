package com.example.project_gym.utilservices;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RawUserNameGeneratorTest {

    @Test
    void generateUserName_shouldJoinFirstAndLastName() {
        RawUserNameGenerator generator = new RawUserNameGenerator();

        assertEquals("Ivan.Ivanov", generator.generateUserName("Ivan", "Ivanov"));
    }
}

