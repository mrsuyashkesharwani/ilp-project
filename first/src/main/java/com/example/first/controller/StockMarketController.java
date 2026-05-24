package com.example.first.controller;

import com.example.first.Dto.*;
import com.example.first.service.StockMarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class StockMarketController {

    private final StockMarketService stockMarketService;

    @GetMapping
    public ResponseEntity<?> getAllStocks() {
        try { return ResponseEntity.ok(stockMarketService.getAllStocks()); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/{stockId}")
    public ResponseEntity<?> getStock(@PathVariable Long stockId) {
        try { return ResponseEntity.ok(stockMarketService.getStockById(stockId)); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }

    @GetMapping("/risk/{level}")
    public ResponseEntity<?> getByRisk(@PathVariable String level) {
        try { return ResponseEntity.ok(stockMarketService.getStocksByRisk(level)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/gainers")
    public ResponseEntity<?> getGainers() {
        try { return ResponseEntity.ok(stockMarketService.getTopGainers()); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/losers")
    public ResponseEntity<?> getLosers() {
        try { return ResponseEntity.ok(stockMarketService.getTopLosers()); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buyStock(@RequestBody BuyStockRequestDto dto) {
        try { return ResponseEntity.ok(stockMarketService.buyStock(dto)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sellStock(@RequestBody SellStockRequestDto dto) {
        try {
            var result = stockMarketService.sellStock(dto);
            return result != null ? ResponseEntity.ok(result) : ResponseEntity.noContent().build();
        }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @GetMapping("/portfolio/{userId}")
    public ResponseEntity<?> getPortfolio(@PathVariable Long userId) {
        try { return ResponseEntity.ok(stockMarketService.getUserPortfolio(userId)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/history/{stockId}")
    public ResponseEntity<?> getHistory(@PathVariable Long stockId) {
        try { return ResponseEntity.ok(stockMarketService.getStockHistory(stockId)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }
}
