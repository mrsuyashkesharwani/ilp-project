package com.example.first.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_history")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class PortfolioHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Double totalValue;
    private Double totalInvestment;
    private Double profitLoss;
    private Double profitLossPercent;
    private LocalDateTime snapshotDate;
}
