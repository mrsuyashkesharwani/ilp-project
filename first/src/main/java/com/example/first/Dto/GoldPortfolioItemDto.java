package com.example.first.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class GoldPortfolioItemDto {
    private Long goldId;
    private String type;
    private Double quantityGrams;
    private Double purchasePricePerGram;
    private Double currentPricePerGram;
    private String storageType;
    private String notes;
    private LocalDate purchaseDate;
    private Double totalInvestment;
    private Double currentValue;
    private Double profitLoss;
    private Double profitLossPercent;
    private Long userId;
}
