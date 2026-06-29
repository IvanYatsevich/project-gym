package com.example.project_gym.mapper;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.model.request.create.TraineeCreateRequest;
import com.example.project_gym.model.request.update.PasswordChangeRequest;
import com.example.project_gym.model.request.update.TraineeUpdateRequest;
import com.example.project_gym.model.request.update.UserActivationRequest;
import com.example.project_gym.model.response.create.TraineeCreateResponse;
import com.example.project_gym.model.response.get.TraineeResponse;
import com.example.project_gym.model.response.get.TraineeTrainerResponse;
import com.example.project_gym.model.response.update.TraineeUpdateResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TraineeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "trainerEntities", ignore = true)
    @Mapping(target = "trainingEntities", ignore = true)
    TraineeEntity toEntity(TraineeCreateRequest traineeCreateRequest);


    @Mapping(target = "username", source = "user.userName")
    @Mapping(target = "password", source = "user.password")
    TraineeCreateResponse toCreateResponse(TraineeEntity traineeEntity);

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.active")
    @Mapping(target = "assignedTrainers", source = "trainerEntities")
    TraineeResponse toResponse(TraineeEntity traineeEntity);

    @Mapping(target = "trainerUsername", source = "user.userName")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "specialization", source = "trainingTypeEntity")
    TraineeTrainerResponse toAssignedTrainerResponse(TrainerEntity trainerEntity);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "trainerEntities", ignore = true)
    @Mapping(target = "trainingEntities", ignore = true)
    void updateEntity(TraineeUpdateRequest request, @MappingTarget TraineeEntity entity);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "trainerEntities", ignore = true)
    @Mapping(target = "trainingEntities", ignore = true)
    @Mapping(target = "user.active", source = "isActive")
    void updateActiveStatus(UserActivationRequest request, @MappingTarget TraineeEntity entity);


    @Mapping(target = "username", source = "user.userName")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.active")
    @Mapping(target = "trainers", source = "trainerEntities")
    TraineeUpdateResponse toUpdateResponse(TraineeEntity traineeEntity);




}
