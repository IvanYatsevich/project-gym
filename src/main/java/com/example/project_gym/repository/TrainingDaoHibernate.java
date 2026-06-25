package com.example.project_gym.repository;

import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.repository.idao.TrainingDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@Primary
public class TrainingDaoHibernate implements TrainingDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TrainingEntity create(TrainingEntity trainingEntity) {
        entityManager.persist(trainingEntity);
        return trainingEntity;
    }

    @Override
    public Optional<TrainingEntity> findById(Long id) {
        return Optional.ofNullable(entityManager.find(TrainingEntity.class, id));
    }

    @Override
    public List<TrainingEntity> getAll() {
        TypedQuery<TrainingEntity> query = entityManager.createQuery(
            "SELECT t FROM TrainingEntity t",
            TrainingEntity.class
        );
        return query.getResultList();
    }
}
