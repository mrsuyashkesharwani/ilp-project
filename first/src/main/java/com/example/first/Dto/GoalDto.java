package com.example.first.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalDto {
    private Long goalId;
    private String name;
    private Double targetAmount;
    private Double savedAmount;
    private String targetDate;
    private String status;
    private String icon;
    private Long userId;
}
