package com.example.first.Dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationDto {
    private String type;        // SAVINGS, INVESTMENT, EXPENSE, RISK, GOAL
    private String title;
    private String message;
    private String priority;    // HIGH, MEDIUM, LOW
    private String icon;        // icon name for frontend
}
