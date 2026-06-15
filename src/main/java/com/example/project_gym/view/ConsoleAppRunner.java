package com.example.project_gym.view;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@ConditionalOnProperty(name = "app.console.enabled", havingValue = "true")
public class ConsoleAppRunner implements CommandLineRunner {

    private final ConsoleCommandHandler commandHandler;

    public ConsoleAppRunner(ConsoleCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Console is enabled. Type 'help' for commands. Type 'exit' to stop.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String output = commandHandler.handle(input);

            if ("exit".equals(output)) {
                System.out.println("Bye");
                return;
            }

            if (!output.isBlank()) {
                System.out.println(output);
            }
        }
    }
}

