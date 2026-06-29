package com.example.project_gym.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "training_types")
@Entity
public class TrainingTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "training_type_id")
    private Long id;

    @Column(name = "training_type", nullable = false)
    private String trainingTypeName;


}
