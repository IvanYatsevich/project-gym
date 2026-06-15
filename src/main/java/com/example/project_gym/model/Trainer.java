package com.example.project_gym.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Trainer extends User{
    private TrainingType trainingType;
    private Long userId;
}
