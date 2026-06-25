package com.example.project_gym.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trainees")
public class TraineeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "trainee_id")
    private Long id;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column
    private String address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "traineeEntities")
    private Set<TrainerEntity> trainerEntities = new HashSet<>();

    @OneToMany(mappedBy = "traineeEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TrainingEntity> trainingEntities;
}

