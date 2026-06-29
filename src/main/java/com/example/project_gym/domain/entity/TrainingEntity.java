package com.example.project_gym.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trainings")
@Entity
public class TrainingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "training_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainee_id")
    private TraineeEntity traineeEntity;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private TrainerEntity trainerEntity;

    @Column(name = "training_name", nullable = false)
    private String trainingName;

    @ManyToOne
    @JoinColumn(name = "training_type_id")
    private TrainingTypeEntity trainingTypeEntity;

    @Column(name = "training_date", nullable = false)
    private LocalDateTime trainingDate;

    @Column(name = "training_duration", nullable = false)
    private Long trainingDuration;


}
