package com.example.project_gym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class ProjectGymApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ProjectGymApplication.class);
        app.setDefaultProperties(Map.of("app.console.enabled", "true"));
        app.run(args);
    }
}
