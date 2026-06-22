package com.example.project_gym.repository;

import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import com.example.project_gym.repository.idao.ITrainerDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@Primary
public class TrainerDaoHibernate implements ITrainerDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainer create(Trainer trainer) {
        entityManager.persist(trainer);
        return trainer;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> selectById(Long id) {
        return Optional.ofNullable(entityManager.find(Trainer.class, id));
    }

    @Override
    public Trainer update(Trainer trainer) {
        return entityManager.merge(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> selectByUsername(String username) {
        TypedQuery<Trainer> query = entityManager.createQuery("SELECT t FROM Trainer t WHERE t.user.userName = :userName", Trainer.class);
        query.setParameter("userName", username);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public boolean deleteByUsername(String username) {
        Optional<Trainer> trainer = selectByUsername(username);
        if (trainer.isPresent()) {
            entityManager.remove(trainer.get());
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainings(String trainerUsername, Date fromDate, Date toDate, String traineeName) {
        StringBuilder query = getTrainerTrainings(fromDate, toDate, traineeName);
        TypedQuery<Training> typedQuery = entityManager.createQuery(query.toString(), Training.class);
        validateAndSetTrainingParameters(trainerUsername, fromDate, toDate, traineeName, typedQuery);

        return typedQuery.getResultList();
    }

    private static void validateAndSetTrainingParameters(String trainerUsername, Date fromDate, Date toDate, String traineeName, TypedQuery<Training> typedQuery) {
        typedQuery.setParameter("trainerUsername", trainerUsername);

        if (fromDate != null) {
            typedQuery.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            typedQuery.setParameter("toDate", toDate);
        }
        if (traineeName != null && !traineeName.isEmpty()) {
            typedQuery.setParameter("traineeName", "%" + traineeName + "%");
        }
    }

    private static StringBuilder getTrainerTrainings(Date fromDate, Date toDate, String traineeName) {
        StringBuilder query = new StringBuilder("SELECT tr FROM Training tr WHERE tr.trainer.user.userName = :trainerUsername");

        if (fromDate != null) {
            query.append(" AND tr.trainingDate >= :fromDate");
        }
        if (toDate != null) {
            query.append(" AND tr.trainingDate <= :toDate");
        }
        if (traineeName != null && !traineeName.isEmpty()) {
            query.append(" AND (LOWER(tr.trainee.user.firstName) LIKE LOWER(:traineeName) OR LOWER(tr.trainee.user.lastName) LIKE LOWER(:traineeName))");
        }
        return query;
    }
}
