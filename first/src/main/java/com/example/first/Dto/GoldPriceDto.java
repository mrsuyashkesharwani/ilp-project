package com.example.first.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class GoldPriceDto {
    private Double currentPrice;
    private Double previousPrice;
    private Double changePercent;
    private Double changeAmount;
    private String trend;           // UP, DOWN, STABLE
    private LocalDateTime lastUpdated;
}
