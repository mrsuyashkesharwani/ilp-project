package com.example.first.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stocks")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockId;

    private String companyName;
    private Double currentPrice;
    private Double previousPrice;
    private String riskLevel;       // LOW, MEDIUM, HIGH
    private Double riskPercent;
    private Double expectedReturn;
    private Double marketCap;       // in crores
    private String sector;          // TECHNOLOGY, BANKING, ENERGY, etc.
    private Double volatility;
    private String stockStatus;     // OPEN, CLOSED
    private Double dailyHigh;
    private Double dailyLow;
    private String marketTrend;     // BULL, BEAR, NEUTRAL
    private LocalDateTime lastUpdated;

    // computed, not persisted
    @Transient
    private Double changePercent;

    @Transient
    private Double changeAmount;

    @JsonIgnore
    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    private List<BuyStock> buyRecords = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    private List<StockHistory> priceHistory = new ArrayList<>();
}
