package com.example.project_gym.mapper;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.model.request.create.TrainerCreateRequest;
import com.example.project_gym.model.request.update.TraineeUpdateRequest;
import com.example.project_gym.model.request.update.TrainerUpdateRequest;
import com.example.project_gym.model.request.update.UserActivationRequest;
import com.example.project_gym.model.response.create.TrainerCreateResponse;
import com.example.project_gym.model.response.get.TraineeResponse;
import com.example.project_gym.model.response.get.TraineeTrainerResponse;
import com.example.project_gym.model.response.get.TrainerResponse;
import com.example.project_gym.model.response.update.TraineeUpdateResponse;
import com.example.project_gym.model.response.update.TrainerUpdateResponse;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface TrainerMapper {


    @Mapping(target = "username", source = "user.userName")
    @Mapping(target = "password", source = "user.password")
    TrainerCreateResponse toCreateResponse(TrainerEntity trainingEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "trainingTypeEntity", ignore = true)
    @Mapping(target = "traineeEntities", ignore = true)
    @Mapping(target = "trainingEntities", ignore = true)
    TrainerEntity toEntity(TrainerCreateRequest trainingCreateRequest);

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.active")
    @Mapping(target = "specialization", source = "trainingTypeEntity")
    @Mapping(target = "trainees", source = "traineeEntities")
    TrainerResponse toGetResponse(TrainerEntity trainerEntity);

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "userName", source = "user.userName")
    com.example.project_gym.model.response.get.TrainerTraineesResponse toTrainerTraineesResponse(TraineeEntity traineeEntity);

    @Mapping(target = "trainerUsername", source = "user.userName")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "specialization", source = "trainingTypeEntity")
    TraineeTrainerResponse toTraineeTrainerResponse(TrainerEntity trainer);

    List<TraineeTrainerResponse> toTraineeTrainerResponses(Set<TrainerEntity> trainers);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainingTypeEntity", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "traineeEntities", ignore = true)
    @Mapping(target = "trainingEntities", ignore = true)
    void updateEntity(TrainerUpdateRequest request, @MappingTarget TrainerEntity entity);


    @Mapping(target = "username", source = "user.userName")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "specialization", source = "trainingTypeEntity")
    @Mapping(target = "isActive", source = "user.active")
    @Mapping(target = "trainees", source = "traineeEntities")
    TrainerUpdateResponse toUpdateResponse(TrainerEntity trainerEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainingTypeEntity", ignore = true)
    @Mapping(target = "traineeEntities", ignore = true)
    @Mapping(target = "trainingEntities", ignore = true)
    @Mapping(target = "user.active", source = "isActive")
    void updateActiveStatus(UserActivationRequest request, @MappingTarget TrainerEntity entity);
}

