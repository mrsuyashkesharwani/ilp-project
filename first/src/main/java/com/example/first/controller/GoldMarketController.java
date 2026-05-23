package com.example.first.controller;

import com.example.first.service.GoldSimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gold")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class GoldMarketController {

    private final GoldSimulationService goldSimulationService;

    @GetMapping("/price")
    public ResponseEntity<?> getCurrentPrice() {
        try { return ResponseEntity.ok(goldSimulationService.getCurrentGoldPrice()); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/price/history")
    public ResponseEntity<?> getPriceHistory() {
        try { return ResponseEntity.ok(goldSimulationService.getGoldHistory()); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/portfolio/{userId}")
    public ResponseEntity<?> getPortfolio(@PathVariable Long userId) {
        try { return ResponseEntity.ok(goldSimulationService.getUserGoldPortfolio(userId)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }
}
