package com.example.first.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoldDto {

    private Long goldId;
    private String type;
    private Double quantityGrams;
    private Double purchasePricePerGram;
    private Double currentPricePerGram;
    private String storageType;
    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    private Long userId;

    // Calculated fields — computed in service, not stored
    private Double totalInvestment;    // quantityGrams * purchasePricePerGram
    private Double currentValue;       // quantityGrams * currentPricePerGram
    private Double profitLoss;         // currentValue - totalInvestment
    private Double profitLossPct;      // (profitLoss / totalInvestment) * 100
}
