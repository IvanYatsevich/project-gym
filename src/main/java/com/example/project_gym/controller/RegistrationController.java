package com.example.project_gym.controller;

import com.example.project_gym.model.request.create.TraineeCreateRequest;
import com.example.project_gym.model.request.create.TrainerCreateRequest;
import com.example.project_gym.model.response.create.TraineeCreateResponse;
import com.example.project_gym.model.response.create.TrainerCreateResponse;
import com.example.project_gym.service.TraineeService;
import com.example.project_gym.service.TrainerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/registration")
@Api(tags = "Registration")
public class RegistrationController {

    private TraineeService traineeService;
    private TrainerService trainerService;

    public RegistrationController(TraineeService traineeService, TrainerService trainerService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    @PostMapping("/new-trainee")
    @ApiOperation("Register a new trainee")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Trainee created"),
            @ApiResponse(code = 400, message = "Validation error")
    })
    public ResponseEntity<TraineeCreateResponse> registerAsTrainee(
            @ApiParam(value = "Trainee registration payload", required = true)
            @RequestBody @Valid TraineeCreateRequest traineeCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(traineeService.create(traineeCreateRequest));
    }

    @PostMapping("/new-trainer")
    @ApiOperation("Register a new trainer")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Trainer created"),
            @ApiResponse(code = 400, message = "Validation error")
    })
    public ResponseEntity<TrainerCreateResponse> registerAsTrainer(
            @ApiParam(value = "Trainer registration payload", required = true)
            @RequestBody @Valid TrainerCreateRequest trainerCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerService.create(trainerCreateRequest));
    }

}





