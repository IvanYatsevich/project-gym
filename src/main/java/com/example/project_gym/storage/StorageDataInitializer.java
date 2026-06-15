package com.example.project_gym.storage;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import com.example.project_gym.model.TrainingType;
import com.example.project_gym.model.dto.initdto.StorageInitData;
import com.example.project_gym.model.dto.initdto.TraineeInitDto;
import com.example.project_gym.model.dto.initdto.TrainingInitDto;
import com.example.project_gym.model.dto.initdto.TrainerInitDto;
import com.example.project_gym.utilservices.UserIdGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Component
public class StorageDataInitializer {

    @Value("${storage.init.file:storage-init-data.json}")
    private String initFile;

    @Autowired
    private UserIdGenerator userIdGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("trainerStorage")
    private Map<Long, Trainer> trainerStorage;

    @Autowired
    @Qualifier("traineeStorage")
    private Map<Long, Trainee> traineeStorage;

    @Autowired
    @Qualifier("trainingStorage")
    private Map<Long, Training> trainingStorage;

    private long trainingKeySeq = 0L;

    @PostConstruct
    public void init() throws IOException {
        String json = readFile(initFile);
        StorageInitData data = objectMapper.readValue(json, StorageInitData.class);

        if (data.trainers() != null) {
            data.trainers().forEach(this::saveTrainer);
        }

        if (data.trainees() != null) {
            data.trainees().forEach(this::saveTrainee);
        }

        if (data.trainings() != null) {
            data.trainings().forEach(this::saveTraining);
        }

        long maxTrainerId = trainerStorage.keySet().stream().mapToLong(Long::longValue).max().orElse(0L);
        long maxTraineeId = traineeStorage.keySet().stream().mapToLong(Long::longValue).max().orElse(0L);
        long maxUserId = Math.max(maxTrainerId, maxTraineeId);

        userIdGenerator.setInitialValue(maxUserId);
    }

    private void saveTrainer(TrainerInitDto trainerInitDto) {
        Trainer trainer = new Trainer();
        trainer.setId(trainerInitDto.id());
        trainer.setFirstName(trainerInitDto.firstName());
        trainer.setLastName(trainerInitDto.lastName());
        trainer.setUserName(trainerInitDto.userName());
        trainer.setPassword(trainerInitDto.password());
        trainer.setActive(trainerInitDto.active());
        trainer.setTrainingType(TrainingType.valueOf(trainerInitDto.trainingType()));
        trainer.setUserId(trainerInitDto.userId());
        trainerStorage.put(trainer.getId(), trainer);
    }

    private void saveTrainee(TraineeInitDto traineeInitDto) {
        Trainee trainee = new Trainee();
        trainee.setId(traineeInitDto.id());
        trainee.setFirstName(traineeInitDto.firstName());
        trainee.setLastName(traineeInitDto.lastName());
        trainee.setUserName(traineeInitDto.userName());
        trainee.setPassword(traineeInitDto.password());
        trainee.setActive(traineeInitDto.active());
        trainee.setDateOfBirth(parseDate(traineeInitDto.dateOfBirth()));
        trainee.setAddress(traineeInitDto.address());
        trainee.setUserId(traineeInitDto.userId());
        traineeStorage.put(trainee.getId(), trainee);
    }

    private void saveTraining(TrainingInitDto trainingInitDto) {
        Training training = new Training();
        training.setTrainerId(trainingInitDto.trainerId());
        training.setTraineeId(trainingInitDto.traineeId());
        training.setTrainingName(trainingInitDto.trainingName());
        training.setTrainingType(TrainingType.valueOf(trainingInitDto.trainingType()));
        training.setTrainingDate(parseDate(trainingInitDto.trainingDate()));
        training.setTrainingDuration(Duration.ofMinutes(trainingInitDto.trainingDuration()));
        trainingStorage.put(++trainingKeySeq, training);
    }

    private String readFile(String fileName) throws IOException {
        try (InputStream in = new ClassPathResource(fileName).getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private Date parseDate(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}