package com.example.project_gym.view;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import com.example.project_gym.model.TrainingType;
import com.example.project_gym.model.dto.dtoin.TraineeDtoIn;
import com.example.project_gym.model.dto.dtoin.TrainerDtoIn;
import com.example.project_gym.model.dto.dtoin.TrainingDtoIn;
import com.example.project_gym.model.dto.dtoupdate.TraineeUpdateDto;
import com.example.project_gym.model.dto.dtoupdate.TrainerUpdateDto;
import com.example.project_gym.service.TraineeService;
import com.example.project_gym.service.TrainerService;
import com.example.project_gym.service.TrainingService;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component
public class GymFacade {

    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;

    public GymFacade(TrainerService trainerService,
                     TraineeService traineeService,
                     TrainingService trainingService) {
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    public Trainer createTrainer(String firstName, String lastName, TrainingType specialization) {
        return trainerService.create(new TrainerDtoIn(firstName, lastName, specialization));
    }

    public Trainer updateTrainer(Long id, Boolean isActive, TrainingType specialization) {
        return trainerService.update(id, new TrainerUpdateDto(isActive, specialization));
    }

    public Trainer getTrainer(Long id) {
        return trainerService.select(id);
    }

    public Trainee createTrainee(String firstName, String lastName, Date dateOfBirth, String address) {
        return traineeService.create(new TraineeDtoIn(firstName, lastName, dateOfBirth, address));
    }

    public Trainee updateTrainee(Long id, Boolean isActive, String address) {
        return traineeService.update(id, new TraineeUpdateDto(isActive, address));
    }

    public boolean deleteTrainee(Long id) {
        return traineeService.delete(id);
    }

    public Trainee getTrainee(Long id) {
        return traineeService.select(id);
    }

    public Training createTraining(Long trainerId,
                                   Long traineeId,
                                   String trainingName,
                                   TrainingType trainingType,
                                   Date trainingDate,
                                   Duration trainingDuration) {
        return trainingService.create(
                new TrainingDtoIn(trainerId, traineeId, trainingName, trainingType, trainingDate, trainingDuration)
        );
    }

    public Training getTraining(Long id) {
        return trainingService.select(id);
    }
}
