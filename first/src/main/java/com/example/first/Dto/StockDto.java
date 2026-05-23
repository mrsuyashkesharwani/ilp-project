package com.example.first.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class StockDto {
    private Long stockId;
    private String companyName;
    private Double currentPrice;
    private Double previousPrice;
    private String riskLevel;
    private Double riskPercent;
    private Double expectedReturn;
    private Double marketCap;
    private String sector;
    private Double volatility;
    private String stockStatus;
    private Double dailyHigh;
    private Double dailyLow;
    private String marketTrend;
    private Double changePercent;   // (current-previous)/previous * 100
    private Double changeAmount;    // current - previous
    private LocalDateTime lastUpdated;
}
