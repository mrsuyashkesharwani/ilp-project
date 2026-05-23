package com.example.first.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gold_price")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class GoldPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceId;

    private Double currentPrice;
    private Double previousPrice;
    private LocalDateTime lastUpdated;
}
