package com.example.project_gym.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsoleAppRunnerTest {

    @Mock
    private ConsoleCommandHandler commandHandler;

    @Test
    void run_shouldHandleInputUntilExit() {
        ConsoleAppRunner runner = new ConsoleAppRunner(commandHandler);

        InputStream oldIn = System.in;
        try {
            String input = "hello\nexit\n";
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

            when(commandHandler.handle("hello")).thenReturn("ok");
            when(commandHandler.handle("exit")).thenReturn("exit");

            runner.run();

            verify(commandHandler).handle("hello");
            verify(commandHandler).handle("exit");
        } finally {
            System.setIn(oldIn);
        }
    }
}

