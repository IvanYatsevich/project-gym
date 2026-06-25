package com.example.project_gym.repository;

import com.example.project_gym.domain.entity.TraineeEntity;
import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.repository.idao.TraineeDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@Primary
public class TraineeDaoHibernate implements TraineeDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TraineeEntity create(TraineeEntity traineeEntity) {
        entityManager.persist(traineeEntity);
        return traineeEntity;
    }

    @Override
    public boolean delete(Long id) {
        TraineeEntity traineeEntity = entityManager.find(TraineeEntity.class, id);
        if (traineeEntity == null) {
            throw new NoSuchElementException("Trainee not found");
        }

        detachFromAssignedTrainers(traineeEntity);
        entityManager.remove(traineeEntity);
        return true;
    }

    @Override
    public Optional<TraineeEntity> findById(Long id) {
        return Optional.ofNullable(entityManager.find(TraineeEntity.class, id));
    }

    @Override
    public TraineeEntity update(TraineeEntity traineeEntity) {
        return entityManager.merge(traineeEntity);
    }

    @Override
    public Optional<TraineeEntity> findByUsername(String username) {
        TypedQuery<TraineeEntity> query = entityManager.createQuery(
                "SELECT t FROM TraineeEntity t WHERE t.user.userName = :userName",
            TraineeEntity.class
        );
        query.setParameter("userName", username);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public boolean deleteByUsername(String username) {
        Optional<TraineeEntity> trainee = findByUsername(username);
        if (trainee.isPresent()) {
            detachFromAssignedTrainers(trainee.get());
            entityManager.remove(trainee.get());
            return true;
        }
        return false;
    }

    @Override
    public List<TrainingEntity> getTrainings(String traineeUsername, Date fromDate, Date toDate, String trainerName, String trainingType) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TrainingEntity> cq = cb.createQuery(TrainingEntity.class);
        Root<TrainingEntity> training = cq.from(TrainingEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(training.get("traineeEntity").get("user").get("userName"), traineeUsername));

        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(training.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(training.get("trainingDate"), toDate));
        }
        if (trainerName != null && !trainerName.isEmpty()) {
            String pattern = "%" + trainerName.toLowerCase() + "%";
            Predicate byFirstName = cb.like(cb.lower(training.get("trainerEntity").get("user").get("firstName")), pattern);
            Predicate byLastName = cb.like(cb.lower(training.get("trainerEntity").get("user").get("lastName")), pattern);
            predicates.add(cb.or(byFirstName, byLastName));
        }
        if (trainingType != null && !trainingType.isEmpty()) {
            predicates.add(cb.equal(training.get("trainingType").get("trainingTypeName"), trainingType));
        }

        cq.select(training).where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<TrainerEntity> findUnassignedTrainers(String traineeUsername) {
        TypedQuery<TrainerEntity> query = entityManager.createQuery(
                "SELECT DISTINCT tr FROM TrainerEntity tr WHERE tr NOT IN (SELECT tr2 FROM TraineeEntity t JOIN t.trainerEntities tr2 WHERE t.user.userName = :traineeUsername)", TrainerEntity.class);
        query.setParameter("traineeUsername", traineeUsername);
        return query.getResultList();
    }

    @Override
    public void updateTrainersList(TraineeEntity traineeEntity, List<TrainerEntity> trainerEntities) {
        TraineeEntity managedTraineeEntity = entityManager.merge(traineeEntity);

        for (TrainerEntity currentTrainerEntity : new HashSet<>(managedTraineeEntity.getTrainerEntities())) {
            currentTrainerEntity.getTraineeEntities().remove(managedTraineeEntity);
        }
        managedTraineeEntity.getTrainerEntities().clear();

        for (TrainerEntity trainerEntity : trainerEntities) {
            TrainerEntity managedTrainerEntity = entityManager.merge(trainerEntity);
            managedTrainerEntity.getTraineeEntities().add(managedTraineeEntity);
            managedTraineeEntity.getTrainerEntities().add(managedTrainerEntity);
        }
    }

    private void detachFromAssignedTrainers(TraineeEntity traineeEntity) {
        for (TrainerEntity trainerEntity : traineeEntity.getTrainerEntities()) {
            trainerEntity.getTraineeEntities().remove(traineeEntity);
        }
        traineeEntity.getTrainerEntities().clear();
    }
}
