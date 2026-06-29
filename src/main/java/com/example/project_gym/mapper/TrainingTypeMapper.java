package com.example.project_gym.mapper;

import com.example.project_gym.domain.entity.TrainingTypeEntity;
import com.example.project_gym.model.response.get.TrainingTypeResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    TrainingTypeResponse toResponse(TrainingTypeEntity trainingTypeEntity);
}
