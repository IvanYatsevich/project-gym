package com.example.project_gym.service;

import com.example.project_gym.mapper.TrainingTypeMapper;
import com.example.project_gym.model.response.get.TrainingTypeResponse;
import com.example.project_gym.repository.idao.TrainingTypeDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingTypeService {

    private TrainingTypeDAO trainingTypeDAO;

    private TrainingTypeMapper trainingTypeMapper;

    public TrainingTypeService(TrainingTypeDAO trainingTypeDAO, TrainingTypeMapper trainingTypeMapper) {
        this.trainingTypeDAO = trainingTypeDAO;
        this.trainingTypeMapper = trainingTypeMapper;
    }

    public List<TrainingTypeResponse> getAll() {
        return trainingTypeDAO.getAll().stream()
                .map(trainingType -> trainingTypeMapper.toResponse(trainingType))
                .toList();
    }
}
