package com.example.first.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationDto {
    private String riskCategory; // LOW / MEDIUM / HIGH
    private Double targetAmount;

    // Percentages
    private Double goldPct;
    private Double stockPct;
    private Double savingsPct;

    // Amounts
    private Double goldAllocatedAmount;
    private Double stockAllocatedAmount;
    private Double savingsAllocatedAmount;

    // Specific recommendations
    private List<String> recommendedStocks;
}
