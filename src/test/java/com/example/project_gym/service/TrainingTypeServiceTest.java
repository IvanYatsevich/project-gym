package com.example.project_gym.service;
import com.example.project_gym.domain.entity.TrainingTypeEntity;
import com.example.project_gym.mapper.TrainingTypeMapper;
import com.example.project_gym.model.response.get.TrainingTypeResponse;
import com.example.project_gym.repository.idao.TrainingTypeDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceTest {
    @Mock
    private TrainingTypeDAO trainingTypeDAO;
    @Mock
    private TrainingTypeMapper trainingTypeMapper;
    private TrainingTypeService service;
    @BeforeEach
    void setUp() {
        service = new TrainingTypeService(trainingTypeDAO, trainingTypeMapper);
    }
    @Test
    void service_shouldBeInitialized() {
        assertNotNull(service);
    }

    @Test
    void getAll_shouldReturnAllTrainingTypes() {
        TrainingTypeEntity entity1 = new TrainingTypeEntity();
        entity1.setId(1L);
        entity1.setTrainingTypeName("CARDIO");
        TrainingTypeEntity entity2 = new TrainingTypeEntity();
        entity2.setId(2L);
        entity2.setTrainingTypeName("STRENGTH");

        TrainingTypeResponse response1 = new TrainingTypeResponse("CARDIO", 1L);
        TrainingTypeResponse response2 = new TrainingTypeResponse("STRENGTH", 2L);

        when(trainingTypeDAO.getAll()).thenReturn(List.of(entity1, entity2));
        when(trainingTypeMapper.toResponse(entity1)).thenReturn(response1);
        when(trainingTypeMapper.toResponse(entity2)).thenReturn(response2);

        List<TrainingTypeResponse> result = service.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("CARDIO", result.get(0).trainingTypeName());
        assertEquals("STRENGTH", result.get(1).trainingTypeName());
    }

    @Test
    void getAll_shouldReturnEmptyListWhenNoTrainingTypes() {
        when(trainingTypeDAO.getAll()).thenReturn(List.of());

        List<TrainingTypeResponse> result = service.getAll();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}