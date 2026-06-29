package com.example.project_gym.repository;

import com.example.project_gym.domain.entity.TrainingTypeEntity;
import com.example.project_gym.repository.idao.TrainingTypeDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class TrainingTypeDaoHibernate implements TrainingTypeDAO {


	@PersistenceContext
	private EntityManager entityManager;



	@Override
	public Optional<TrainingTypeEntity> findByTrainingTypeName(String trainingTypeName) {
		TypedQuery<TrainingTypeEntity> query = entityManager.createQuery(
				"SELECT tt FROM TrainingTypeEntity tt WHERE LOWER(tt.trainingTypeName) = LOWER(:trainingTypeName)",
				TrainingTypeEntity.class
		);
		query.setParameter("trainingTypeName", trainingTypeName);
		return query.getResultList().stream().findFirst();
	}

	@Override
	public List<TrainingTypeEntity> getAll() {
		TypedQuery<TrainingTypeEntity> query = entityManager.createQuery(
				"SELECT tp FROM TrainingTypeEntity tp", TrainingTypeEntity.class);
		return query.getResultList();
	}
}
