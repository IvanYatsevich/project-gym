package com.example.project_gym.controller;

import com.example.project_gym.model.response.get.TrainingTypeResponse;
import com.example.project_gym.service.TrainingTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/training-types")
@Api(tags = "Training Types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    public TrainingTypeController(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }

    @GetMapping
    @ApiOperation("Get all training types")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Training types returned")
    })
    public ResponseEntity<List<TrainingTypeResponse>> getAll() {
        return ResponseEntity.ok(trainingTypeService.getAll());
    }
}

