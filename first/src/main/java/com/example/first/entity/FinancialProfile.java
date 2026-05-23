package com.example.first.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_profile")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class FinancialProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private Integer financialScore;
    private Double riskPercentage;
    private Double totalSavings;
    private String riskLevel;       // LOW, MEDIUM, HIGH
    private String currentStatus;   // Excellent, Good, Fair, Needs Improvement
    private LocalDateTime updatedAt;
}
