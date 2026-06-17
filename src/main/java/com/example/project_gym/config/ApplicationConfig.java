package com.example.project_gym.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.example.project_gym")
@PropertySource("classpath:application.properties")
public class ApplicationConfig {
}
