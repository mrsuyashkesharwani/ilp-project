package com.example.first.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MarketEventDto {
    private Long eventId;
    private String eventName;
    private String description;
    private String marketEffect;    // BULL, BEAR
    private Double magnitude;
    private String sector;
    private boolean active;
    private LocalDateTime createdAt;
}
