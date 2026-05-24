package com.example.first.controller;

import com.example.first.repo.*;
import com.example.first.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class PortfolioController {

    private final StockMarketService stockMarketService;
    private final GoldSimulationService goldSimulationService;

    @GetMapping("/{userId}/summary")
    public ResponseEntity<?> getSummary(@PathVariable Long userId) {
        try {
            var stocks = stockMarketService.getUserPortfolio(userId);
            var gold = goldSimulationService.getUserGoldPortfolio(userId);

            double stockInv = stocks.stream().mapToDouble(s -> s.getTotalInvestment()).sum();
            double stockVal = stocks.stream().mapToDouble(s -> s.getCurrentValue()).sum();
            double goldInv  = gold.stream().mapToDouble(g -> g.getTotalInvestment()).sum();
            double goldVal  = gold.stream().mapToDouble(g -> g.getCurrentValue()).sum();

            double totalInv = stockInv + goldInv;
            double totalVal = stockVal + goldVal;
            double totalPL  = totalVal - totalInv;
            double totalPLPct = totalInv == 0 ? 0 : (totalPL / totalInv) * 100;

            Map<String, Object> summary = new HashMap<>();
            summary.put("totalStockInvestment", Math.round(stockInv * 100.0) / 100.0);
            summary.put("totalStockValue", Math.round(stockVal * 100.0) / 100.0);
            summary.put("totalStockPL", Math.round((stockVal - stockInv) * 100.0) / 100.0);
            summary.put("totalGoldInvestment", Math.round(goldInv * 100.0) / 100.0);
            summary.put("totalGoldValue", Math.round(goldVal * 100.0) / 100.0);
            summary.put("totalGoldPL", Math.round((goldVal - goldInv) * 100.0) / 100.0);
            summary.put("totalPortfolioInvestment", Math.round(totalInv * 100.0) / 100.0);
            summary.put("totalPortfolioValue", Math.round(totalVal * 100.0) / 100.0);
            summary.put("totalPortfolioPL", Math.round(totalPL * 100.0) / 100.0);
            summary.put("totalPortfolioPLPercent", Math.round(totalPLPct * 100.0) / 100.0);
            return ResponseEntity.ok(summary);
        }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/{userId}/stocks")
    public ResponseEntity<?> getStocks(@PathVariable Long userId) {
        try { return ResponseEntity.ok(stockMarketService.getUserPortfolio(userId)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/{userId}/gold")
    public ResponseEntity<?> getGold(@PathVariable Long userId) {
        try { return ResponseEntity.ok(goldSimulationService.getUserGoldPortfolio(userId)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }
}
