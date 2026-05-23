package com.example.first.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "market_events")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class MarketEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String eventName;
    private String description;
    private String marketEffect;    // BULL, BEAR
    private Double magnitude;       // additional % impact
    private String sector;          // ALL, TECHNOLOGY, BANKING, ENERGY, AUTOMOBILE, FMCG, FOOD_TECH, INFRASTRUCTURE
    private boolean active;
    private LocalDateTime createdAt;
}
