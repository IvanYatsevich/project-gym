package com.example.project_gym.controller;

import com.example.project_gym.model.request.TraineeTrainingsRequest;
import com.example.project_gym.model.request.get.TraineeRequest;
import com.example.project_gym.model.request.get.TrainerRequest;
import com.example.project_gym.model.request.update.TraineeTrainersUpdateRequest;
import com.example.project_gym.model.request.update.TraineeUpdateRequest;
import com.example.project_gym.model.request.update.UserActivationRequest;
import com.example.project_gym.model.response.get.SimpleTrainingResponse;
import com.example.project_gym.model.response.get.TraineeResponse;
import com.example.project_gym.model.response.get.TraineeTrainerResponse;
import com.example.project_gym.model.response.update.TraineeTrainersUpdateResponse;
import com.example.project_gym.model.response.update.TraineeUpdateResponse;
import com.example.project_gym.service.TraineeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trainees")
@Validated
@Api(tags = "Trainees")
public class TraineeController {

    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @GetMapping("/{username}")
    @ApiOperation("Get trainee profile by username")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Profile returned"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<TraineeResponse> getProfile(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable("username") @Valid String username) {
        return ResponseEntity.ok(traineeService.selectByUsername(new TraineeRequest(username)));
    }

    @PutMapping
    @ApiOperation("Update trainee profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Profile updated"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<TraineeUpdateResponse> updateProfile(
            @ApiParam(value = "Trainee update payload", required = true)
            @RequestBody @Valid TraineeUpdateRequest request) {
        return ResponseEntity.ok(traineeService.update(request));
    }

    @DeleteMapping("/{username}")
    @ApiOperation("Delete trainee profile by username")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Profile deleted")
    })
    public ResponseEntity<Void> deleteProfile(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable @NotBlank String username) {
        traineeService.deleteByUsername(new TraineeRequest(username));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/unassigned-active-trainers")
    @ApiOperation("Get unassigned active trainers for trainee")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainers returned")
    })
    public ResponseEntity<List<TraineeTrainerResponse>> getUnassignedActiveTrainers(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable("username") @NotBlank String username) {
        return ResponseEntity.ok(traineeService.getUnassignedTrainers(new TraineeRequest(username)));
    }

    @PutMapping("/trainers")
    @ApiOperation("Update trainee trainers list")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer list updated"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 404, message = "Trainee or trainer not found")
    })
    public ResponseEntity<TraineeTrainersUpdateResponse> updateTrainersList(
            @ApiParam(value = "Trainers update payload", required = true)
            @RequestBody @Valid TraineeTrainersUpdateRequest request) {
        return ResponseEntity.ok(traineeService.updateTrainersList(request));
    }

    @GetMapping("/{username}/trainings")
    @ApiOperation("Get trainee trainings with optional filters")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainings returned")
    })
    public ResponseEntity<List<SimpleTrainingResponse>> getTrainings(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable("username") @NotBlank String username,
            @ApiParam(value = "Filter from date-time in ISO format")
            @RequestParam(value = "fromDate", required = false) LocalDateTime fromDate,
            @ApiParam(value = "Filter to date-time in ISO format")
            @RequestParam(value = "toDate", required = false) LocalDateTime toDate,
            @ApiParam(value = "Filter by trainer username")
            @RequestParam(value = "trainerName", required = false) String trainerName,
            @ApiParam(value = "Filter by training type")
            @RequestParam(value = "trainingType", required = false) String trainingType) {
        TraineeTrainingsRequest request = new TraineeTrainingsRequest(username, fromDate, toDate, trainerName, trainingType);
        return ResponseEntity.ok(traineeService.getTrainings(request));
    }

    @PatchMapping("/activation")
    @ApiOperation("Activate or deactivate trainee")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Activation status changed"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<Void> activateDeactivate(
            @ApiParam(value = "Activation payload", required = true)
            @RequestBody @Valid UserActivationRequest request) {
        traineeService.activateDeactivate(request);
        return ResponseEntity.ok().build();
    }
}

