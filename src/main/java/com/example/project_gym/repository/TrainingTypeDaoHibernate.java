package com.example.project_gym.repository;

import com.example.project_gym.model.TrainingType;
import com.example.project_gym.repository.idao.ITrainingTypeDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class TrainingTypeDaoHibernate implements ITrainingTypeDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Optional<TrainingType> findByTrainingTypeName(String trainingTypeName) {
		TypedQuery<TrainingType> query = entityManager.createQuery(
				"SELECT tt FROM TrainingType tt WHERE LOWER(tt.trainingTypeName) = LOWER(:trainingTypeName)",
				TrainingType.class
		);
		query.setParameter("trainingTypeName", trainingTypeName);
		return query.getResultList().stream().findFirst();
	}
}
