package com.example.project_gym.repository;

import com.example.project_gym.domain.entity.TrainerEntity;
import com.example.project_gym.domain.entity.TrainingEntity;
import com.example.project_gym.repository.idao.TrainerDAO;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDaoHibernate implements TrainerDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TrainerEntity create(TrainerEntity trainerEntity) {
        entityManager.persist(trainerEntity);
        return trainerEntity;
    }

    @Override
    public Optional<TrainerEntity> findById(Long id) {
        return Optional.ofNullable(entityManager.find(TrainerEntity.class, id));
    }

    @Override
    public TrainerEntity update(TrainerEntity trainerEntity) {
        return entityManager.merge(trainerEntity);
    }

    @Override
    public Optional<TrainerEntity> findByUsername(String username) {
        TypedQuery<TrainerEntity> query = entityManager.createQuery("SELECT t FROM TrainerEntity t WHERE t.user.userName = :userName", TrainerEntity.class);
        query.setParameter("userName", username);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public boolean deleteByUsername(String username) {
        Optional<TrainerEntity> trainer = findByUsername(username);
        if (trainer.isPresent()) {
            entityManager.remove(trainer.get());
            return true;
        }
        return false;
    }

    @Override
    public List<TrainingEntity> getTrainings(String trainerUsername, LocalDateTime fromDate, LocalDateTime toDate, String traineeName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TrainingEntity> cq = cb.createQuery(TrainingEntity.class);
        Root<TrainingEntity> training = cq.from(TrainingEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(training.get("trainerEntity").get("user").get("userName"), trainerUsername));

        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(training.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(training.get("trainingDate"), toDate));
        }
        if (traineeName != null && !traineeName.isEmpty()) {
            String pattern = "%" + traineeName.toLowerCase() + "%";
            Predicate byFirstName = cb.like(cb.lower(training.get("traineeEntity").get("user").get("firstName")), pattern);
            Predicate byLastName = cb.like(cb.lower(training.get("traineeEntity").get("user").get("lastName")), pattern);
            predicates.add(cb.or(byFirstName, byLastName));
        }

        cq.select(training).where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(cq).getResultList();
    }
}
