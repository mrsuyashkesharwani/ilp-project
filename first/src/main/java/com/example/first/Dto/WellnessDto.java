package com.example.first.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class WellnessDto {
    private Integer score;
    private String level;           // Excellent, Good, Fair, Needs Improvement
    private Double savingsRate;
    private Double investmentRate;
    private Double totalIncome;
    private Double totalExpenses;
    private Double totalInvested;
    private Double totalSaved;
    private Double riskScore;
    private String riskLevel;       // LOW, MEDIUM, HIGH
    private Double diversificationScore;
    private List<String> recommendations;
    private Integer goalCompletionRate;
}
