package com.example.project_gym.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed",
                request.getRequestURI(), details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed",
                request.getRequestURI(), details);
    }

    @ExceptionHandler({
            EntityNotFoundException.class,
            TraineeNotFoundException.class,
            TrainerNotFoundException.class,
            TrainingNotFoundException.class,
            TrainingTypeNotFoundException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<ApiError> handleEntityNotFound(RuntimeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(),
                request.getRequestURI(), List.of());
    }

    @ExceptionHandler({AuthenticationFailedException.class, UnauthenticatedException.class})
    public ResponseEntity<ApiError> handleAuthentication(RuntimeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(),
                request.getRequestURI(), List.of());
    }

    @ExceptionHandler(TrainingConflictException.class)
    public ResponseEntity<ApiError> handleTrainingConflict(TrainingConflictException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(),
                request.getRequestURI(), List.of());
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ApiError> handleDuplicateUsername(DuplicateUsernameException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(),
                request.getRequestURI(), List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(),
                request.getRequestURI(), List.of());
    }

    @ExceptionHandler({
            BusinessRuleException.class,
            InvalidTrainerAssignmentException.class,
            InactiveUserException.class,
            InvalidTrainingDataException.class,
            PasswordMismatchException.class,
            InvalidPasswordChangeException.class
    })
    public ResponseEntity<ApiError> handleBusinessRule(RuntimeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(),
                request.getRequestURI(), List.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMessageNotReadable(HttpMessageNotReadableException ex,
                                                             HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed request body",
                request.getRequestURI(), List.of(ex.getMostSpecificCause().getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOtherExceptions(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
                request.getRequestURI(), List.of(ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage()));
    }


    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message,
                                                   String path, List<String> details) {
        ApiError body = new ApiError(status.value(),
                status.getReasonPhrase(),
                message,
                path,
                LocalDateTime.now(),
                details);

        return ResponseEntity.status(status).body(body);
    }
}

