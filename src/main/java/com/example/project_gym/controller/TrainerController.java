package com.example.project_gym.controller;

import com.example.project_gym.model.request.TrainerTrainingsRequest;
import com.example.project_gym.model.request.get.TrainerRequest;
import com.example.project_gym.model.request.update.TrainerUpdateRequest;
import com.example.project_gym.model.request.update.UserActivationRequest;
import com.example.project_gym.model.response.get.SimpleTrainingResponse;
import com.example.project_gym.model.response.get.TrainerResponse;
import com.example.project_gym.model.response.update.TrainerUpdateResponse;
import com.example.project_gym.service.TrainerService;
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
@RequestMapping("/api/v1/trainers")
@Validated
@Api(tags = "Trainers")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping("/{username}")
    @ApiOperation("Get trainer profile by username")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Profile returned"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    public ResponseEntity<TrainerResponse> getProfile(
            @ApiParam(value = "Trainer username", required = true)
            @PathVariable("username") @NotBlank String username) {
        return ResponseEntity.ok(trainerService.selectByUsername(new TrainerRequest(username)));
    }

    @PutMapping
    @ApiOperation("Update trainer profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Profile updated"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    public ResponseEntity<TrainerUpdateResponse> updateProfile(
            @ApiParam(value = "Trainer update payload", required = true)
            @RequestBody @Valid TrainerUpdateRequest request) {
        return ResponseEntity.ok(trainerService.update(request));
    }

    @GetMapping("/{username}/trainings")
    @ApiOperation("Get trainer trainings with optional filters")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainings returned")
    })
    public ResponseEntity<List<SimpleTrainingResponse>> getTrainings(
            @ApiParam(value = "Trainer username", required = true)
            @PathVariable("username") @NotBlank String username,
            @ApiParam(value = "Filter from date-time in ISO format")
            @RequestParam(required = false) LocalDateTime fromDate,
            @ApiParam(value = "Filter to date-time in ISO format")
            @RequestParam(required = false) LocalDateTime toDate,
            @ApiParam(value = "Filter by trainee username")
            @RequestParam(required = false) String traineeName) {
        TrainerTrainingsRequest request = new TrainerTrainingsRequest(username, fromDate, toDate, traineeName);
        return ResponseEntity.ok(trainerService.getTrainings(request));
    }

    @PatchMapping("/activation")
    @ApiOperation("Activate or deactivate trainer")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Activation status changed"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    public ResponseEntity<Void> activateDeactivate(
            @ApiParam(value = "Activation payload", required = true)
            @RequestBody @Valid UserActivationRequest request) {
        trainerService.toggleActive(request);
        return ResponseEntity.ok().build();
    }
}

