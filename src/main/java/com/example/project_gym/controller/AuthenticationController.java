package com.example.project_gym.controller;

import com.example.project_gym.model.request.LoginRequest;
import com.example.project_gym.model.request.update.PasswordChangeRequest;
import com.example.project_gym.service.AuthenticationService;
import com.example.project_gym.utilservices.authenticatedservices.PasswordChangeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Api(tags = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authService;
    private final PasswordChangeService passwordChangeService;

    public AuthenticationController(AuthenticationService authService,
                                    PasswordChangeService passwordChangeService) {
        this.authService = authService;
        this.passwordChangeService = passwordChangeService;
    }

    @GetMapping("/login")
    @ApiOperation("Authenticate user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Authenticated"),
            @ApiResponse(code = 401, message = "Invalid credentials")
    })
    public ResponseEntity<Void> login(
            @ApiParam(value = "Username", required = true)
            @RequestParam("username") @NotBlank String username,
            @ApiParam(value = "Password", required = true)
            @RequestParam("password") @NotBlank String password) {
        authService.authenticate(new LoginRequest(username, password));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-login")
    @ApiOperation("Change user password")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Password changed"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public ResponseEntity<Void> changeLogin(
            @ApiParam(value = "Password change payload", required = true)
            @RequestBody @Valid PasswordChangeRequest request) {
        try {
            passwordChangeService.changeTraineePassword(request);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ignored) {
            passwordChangeService.changeTrainerPassword(request);
            return ResponseEntity.ok().build();
        }
    }
}
