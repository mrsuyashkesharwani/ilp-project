package com.example.first.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId;

    private String name;
    private Double targetAmount;
    private Double savedAmount;
    private String targetDate;
    private String status;
    private String icon;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
