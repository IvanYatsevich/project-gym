package com.example.project_gym;


import com.example.project_gym.config.ApplicationConfig;
import com.example.project_gym.config.PersistenceConfig;
import com.example.project_gym.view.ConsoleAppRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class ProjectGymApplication {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(ApplicationConfig.class, PersistenceConfig.class)) {
            ConsoleAppRunner runner = context.getBean(ConsoleAppRunner.class);
            runner.start();
        }
    }
}
