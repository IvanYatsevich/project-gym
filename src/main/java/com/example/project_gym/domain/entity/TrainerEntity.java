package com.example.project_gym.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trainers")
@Entity
public class TrainerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "trainer_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingTypeEntity trainingTypeEntity;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(name = "trainer_trainees",
            joinColumns = @JoinColumn(name = "trainer_id"),
            inverseJoinColumns = @JoinColumn(name = "trainee_id"))
    private Set<TraineeEntity> traineeEntities = new HashSet<>();

    @OneToMany(mappedBy = "trainerEntity")
    private List<TrainingEntity> trainingEntities;
}
