package com.example.project_gym.controller;

import com.example.project_gym.model.request.create.TrainingCreateRequest;
import com.example.project_gym.service.TrainingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trainings")
@Api(tags = "Trainings")
public class TrainingController {

    private TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping()
    @ApiOperation("Create training")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Training created"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 404, message = "Trainer or trainee not found")
    })
    public ResponseEntity<Void> createTraining(
            @ApiParam(value = "Training creation payload", required = true)
            @RequestBody @Valid TrainingCreateRequest request) {
        trainingService.create(request);
        return ResponseEntity.ok().build();
    }
}
