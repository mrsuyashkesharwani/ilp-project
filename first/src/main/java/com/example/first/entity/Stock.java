package com.example.first.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockId;

    private String companyName;
    private String symbol;
    private Double currentPrice;
    private Double previousPrice;
    private String riskCategory; // LOW / MEDIUM / HIGH
    private Double riskPercent; // For compatibility
    private Double expectedReturn;
    private Double marketCap;
    private String sector;
    private Double volatility;
    private String stockStatus; // OPEN / CLOSED
    private LocalDateTime lastUpdated;
}