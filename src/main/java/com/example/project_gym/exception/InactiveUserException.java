package com.example.project_gym.exception;

public class InactiveUserException extends BusinessRuleException {
    public InactiveUserException(String message) {
        super(message);
    }
}

