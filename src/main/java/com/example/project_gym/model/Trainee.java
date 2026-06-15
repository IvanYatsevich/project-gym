package com.example.project_gym.model;

import lombok.*;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Trainee extends User{
    private Date dateOfBirth;
    private String address;
    private Long userId;
}

