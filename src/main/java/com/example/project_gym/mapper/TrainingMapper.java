package com.example.project_gym.mapper;

import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.model.request.create.TrainingCreateRequest;
import com.example.project_gym.model.response.get.SimpleTrainingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingMapper {



    @Mapping(target = "trainingTypeEntity", source = "trainingTypeEntity")
    @Mapping(target = "nameOfUser", source = "trainerEntity.user.userName")
    SimpleTrainingResponse toResponse(TrainingEntity trainingEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "traineeEntity", ignore = true)
    @Mapping(target = "trainerEntity", ignore = true)
    @Mapping(target = "trainingTypeEntity", ignore = true)
    TrainingEntity toEntity(TrainingCreateRequest trainingCreateRequest);



}
