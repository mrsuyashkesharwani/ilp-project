package com.example.first.controller;

import com.example.first.Dto.MarketEventDto;
import com.example.first.repo.MarketEventRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/market")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class MarketController {

    private final MarketEventRepo marketEventRepo;

    @GetMapping("/events")
    public ResponseEntity<?> getEvents() {
        try {
            List<MarketEventDto> events = marketEventRepo.findTop10ByOrderByCreatedAtDesc()
                .stream().map(e -> {
                    MarketEventDto dto = new MarketEventDto();
                    dto.setEventId(e.getEventId());
                    dto.setEventName(e.getEventName());
                    dto.setDescription(e.getDescription());
                    dto.setMarketEffect(e.getMarketEffect());
                    dto.setMagnitude(e.getMagnitude());
                    dto.setSector(e.getSector());
                    dto.setActive(e.isActive());
                    dto.setCreatedAt(e.getCreatedAt());
                    return dto;
                }).collect(Collectors.toList());
            return ResponseEntity.ok(events);
        }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/events/active")
    public ResponseEntity<?> getActiveEvents() {
        try {
            List<MarketEventDto> events = marketEventRepo.findByActive(true)
                .stream().map(e -> {
                    MarketEventDto dto = new MarketEventDto();
                    dto.setEventId(e.getEventId());
                    dto.setEventName(e.getEventName());
                    dto.setDescription(e.getDescription());
                    dto.setMarketEffect(e.getMarketEffect());
                    dto.setMagnitude(e.getMagnitude());
                    dto.setSector(e.getSector());
                    dto.setActive(e.isActive());
                    dto.setCreatedAt(e.getCreatedAt());
                    return dto;
                }).collect(Collectors.toList());
            return ResponseEntity.ok(events);
        }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getMarketStatus() {
        return ResponseEntity.ok(Map.of(
            "status", "OPEN",
            "message", "Market is open for trading",
            "timestamp", LocalDateTime.now().toString()
        ));
    }
}
