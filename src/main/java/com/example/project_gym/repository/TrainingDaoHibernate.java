package com.example.project_gym.repository;

import com.example.project_gym.model.Training;
import com.example.project_gym.repository.idao.ITrainingDAO;
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
public class TrainingDaoHibernate implements ITrainingDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Training create(Training training) {
        entityManager.persist(training);
        return training;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Training> getById(Long id) {
        return Optional.ofNullable(entityManager.find(Training.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getAll() {
        TypedQuery<Training> query = entityManager.createQuery(
            "SELECT t FROM Training t",
            Training.class
        );
        return query.getResultList();
    }
}
