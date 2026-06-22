package com.example.project_gym.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleAppRunner {

    private static final Logger log = LoggerFactory.getLogger(ConsoleAppRunner.class);

    private final ConsoleCommandHandler commandHandler;
    private final boolean consoleEnabled;

    public ConsoleAppRunner(ConsoleCommandHandler commandHandler, @Value("${app.console.enabled:true}")
     boolean consoleEnabled) {
        this.commandHandler = commandHandler;
        this.consoleEnabled = consoleEnabled;
    }

    public void start() {
        if (!consoleEnabled) {
            return;
        }
        log.info("Type 'help' to see commands. Type 'exit' to finish");
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();
                String output = commandHandler.handle(input);

                if (output != null && !output.isBlank()) {
                    System.out.println(output);
                }

                if ("exit".equalsIgnoreCase(output)) {
                    break;
                }
            }
        }
    }
}
