package com.example.project_gym.exception;

public class InvalidPasswordChangeException extends BusinessRuleException {
    public InvalidPasswordChangeException(String message) {
        super(message);
    }
}

