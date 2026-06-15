package com.example.project_gym.utilservices;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserIdGeneratorTest {

    @Test
    void generateId_shouldIncrementByOneEachCall() {
        UserIdGenerator generator = new UserIdGenerator();

        assertEquals(1L, generator.generateId());
        assertEquals(2L, generator.generateId());
        assertEquals(3L, generator.generateId());
    }

    @Test
    void setInitialValue_shouldMoveCounterForwardOnly() {
        UserIdGenerator generator = new UserIdGenerator();

        generator.setInitialValue(10L);
        assertEquals(11L, generator.generateId());

        generator.setInitialValue(3L);
        assertEquals(12L, generator.generateId());
    }
}
