package com.example.project_gym.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp,
        List<String> details
) {
}

