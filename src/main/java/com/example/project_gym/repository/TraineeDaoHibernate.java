package com.example.project_gym.repository;

import com.example.project_gym.model.Trainee;
import com.example.project_gym.model.Trainer;
import com.example.project_gym.model.Training;
import com.example.project_gym.repository.idao.ITraineeDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@Transactional
@Primary
public class TraineeDaoHibernate implements ITraineeDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainee create(Trainee trainee) {
        entityManager.persist(trainee);
        return trainee;
    }

    @Override
    public boolean delete(Long id) {
        Trainee trainee = entityManager.find(Trainee.class, id);
        if (trainee == null) {
            throw new NoSuchElementException("Trainee not found");
        }

        detachFromAssignedTrainers(trainee);
        entityManager.remove(trainee);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> selectById(Long id) {
        return Optional.ofNullable(entityManager.find(Trainee.class, id));
    }

    @Override
    public Trainee update(Trainee trainee) {
        return entityManager.merge(trainee);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> selectByUsername(String username) {
        TypedQuery<Trainee> query = entityManager.createQuery(
            "SELECT t FROM Trainee t WHERE t.user.userName = :userName",
            Trainee.class
        );
        query.setParameter("userName", username);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public boolean deleteByUsername(String username) {
        Optional<Trainee> trainee = selectByUsername(username);
        if (trainee.isPresent()) {
            detachFromAssignedTrainers(trainee.get());
            entityManager.remove(trainee.get());
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainings(String traineeUsername, Date fromDate, Date toDate, String trainerName, String trainingType) {
        StringBuilder query = new StringBuilder("SELECT tr FROM Training tr WHERE tr.trainee.user.userName = :traineeUsername");

        validateAndAppendTrainingParameters(fromDate, toDate, trainerName, trainingType, query);

        TypedQuery<Training> typedQuery = entityManager.createQuery(query.toString(), Training.class);
        typedQuery.setParameter("traineeUsername", traineeUsername);

        setTrainingParameters(fromDate, toDate, trainerName, trainingType, typedQuery);

        return typedQuery.getResultList();
    }

    private static void setTrainingParameters(Date fromDate, Date toDate, String trainerName, String trainingType, TypedQuery<Training> typedQuery) {
        if (fromDate != null) {
            typedQuery.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            typedQuery.setParameter("toDate", toDate);
        }
        if (trainerName != null && !trainerName.isEmpty()) {
            typedQuery.setParameter("trainerName", "%" + trainerName + "%");
        }
        if (trainingType != null && !trainingType.isEmpty()) {
            typedQuery.setParameter("trainingType", trainingType);
        }
    }

    private static void validateAndAppendTrainingParameters(Date fromDate, Date toDate, String trainerName, String trainingType, StringBuilder query) {
        if (fromDate != null) {
            query.append(" AND tr.trainingDate >= :fromDate");
        }
        if (toDate != null) {
            query.append(" AND tr.trainingDate <= :toDate");
        }
        if (trainerName != null && !trainerName.isEmpty()) {
            query.append(" AND (LOWER(tr.trainer.user.firstName) LIKE LOWER(:trainerName) OR LOWER(tr.trainer.user.lastName) LIKE LOWER(:trainerName))");
        }
        if (trainingType != null && !trainingType.isEmpty()) {
            query.append(" AND tr.trainingType.trainingTypeName = :trainingType");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> findUnassignedTrainers(String traineeUsername) {
        TypedQuery<Trainer> query = entityManager.createQuery(
            "SELECT DISTINCT tr FROM Trainer tr WHERE tr NOT IN (SELECT tr2 FROM Trainee t JOIN t.trainers tr2 WHERE t.user.userName = :traineeUsername)", Trainer.class);
        query.setParameter("traineeUsername", traineeUsername);
        return query.getResultList();
    }

    @Override
    public void updateTrainersList(Trainee trainee, List<Trainer> trainers) {
        Trainee managedTrainee = entityManager.merge(trainee);

        for (Trainer currentTrainer : new HashSet<>(managedTrainee.getTrainers())) {
            currentTrainer.getTrainees().remove(managedTrainee);
        }
        managedTrainee.getTrainers().clear();

        for (Trainer trainer : trainers) {
            Trainer managedTrainer = entityManager.merge(trainer);
            managedTrainer.getTrainees().add(managedTrainee);
            managedTrainee.getTrainers().add(managedTrainer);
        }
    }

    private void detachFromAssignedTrainers(Trainee trainee) {
        for (Trainer trainer : trainee.getTrainers()) {
            trainer.getTrainees().remove(trainee);
        }
        trainee.getTrainers().clear();
    }
}
