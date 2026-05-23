package com.example.first.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioItemDto {
    private Long buyStockId;
    private Long stockId;
    private String companyName;
    private String sector;
    private String riskLevel;
    private Integer quantity;
    private Double buyPrice;
    private Double currentPrice;
    private Double totalInvestment;     // quantity * buyPrice
    private Double currentValue;        // quantity * currentPrice
    private Double profitLoss;          // currentValue - totalInvestment
    private Double profitLossPercent;   // profitLoss / totalInvestment * 100
}
