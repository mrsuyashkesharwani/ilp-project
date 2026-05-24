package com.example.first.controller;

import com.example.first.entity.Stock;
import com.example.first.entity.SystemConfig;
import com.example.first.entity.User;
import com.example.first.repo.StockRepo;
import com.example.first.repo.SystemConfigRepo;
import com.example.first.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepo userRepo;
    private final StockRepo stockRepo;
    private final SystemConfigRepo configRepo;

    // ---- USER MANAGEMENT ----

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    @PatchMapping("/users/{userId}/block")
    public ResponseEntity<String> blockUser(@PathVariable Long userId) {
        return userRepo.findById(userId).map(u -> {
            u.setStatus("BLOCKED");
            userRepo.save(u);
            return ResponseEntity.ok("User blocked successfully");
        }).orElse(ResponseEntity.status(404).body("User not found"));
    }

    @PatchMapping("/users/{userId}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable Long userId) {
        return userRepo.findById(userId).map(u -> {
            u.setStatus("ACTIVE");
            userRepo.save(u);
            return ResponseEntity.ok("User unblocked successfully");
        }).orElse(ResponseEntity.status(404).body("User not found"));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        if (userRepo.existsById(userId)) {
            userRepo.deleteById(userId);
            return ResponseEntity.ok("User deleted successfully");
        }
        return ResponseEntity.status(404).body("User not found");
    }

    // ---- STOCK MANAGEMENT ----

    @GetMapping("/stocks")
    public ResponseEntity<List<Stock>> getAllStocks() {
        return ResponseEntity.ok(stockRepo.findAll());
    }

    @PatchMapping("/stocks/{stockId}/price")
    public ResponseEntity<String> setStockPrice(@PathVariable Long stockId,
                                                @RequestParam Double price) {
        return stockRepo.findById(stockId).map(s -> {
            s.setPreviousPrice(s.getCurrentPrice());
            s.setCurrentPrice(price);
            stockRepo.save(s);
            return ResponseEntity.ok("Stock price updated to ₹" + price);
        }).orElse(ResponseEntity.status(404).body("Stock not found"));
    }

    @PatchMapping("/stocks/{stockId}/status")
    public ResponseEntity<String> setStockStatus(@PathVariable Long stockId,
                                                 @RequestParam String status) {
        return stockRepo.findById(stockId).map(s -> {
            s.setStockStatus(status.toUpperCase());
            stockRepo.save(s);
            return ResponseEntity.ok("Stock status set to " + status);
        }).orElse(ResponseEntity.status(404).body("Stock not found"));
    }

    // ---- MARKET CONFIG ----

    @GetMapping("/config")
    public ResponseEntity<List<SystemConfig>> getAllConfigs() {
        return ResponseEntity.ok(configRepo.findAll());
    }

    @PatchMapping("/config/{key}")
    public ResponseEntity<String> updateConfig(@PathVariable String key,
                                               @RequestBody Map<String, String> body) {
        String value = body.get("value");
        if (value == null) {
            return ResponseEntity.status(400).body("Missing 'value' in request body");
        }
        SystemConfig config = configRepo.findById(key)
                .orElse(new SystemConfig(key, value));
        config.setConfigValue(value);
        configRepo.save(config);
        return ResponseEntity.ok("Config [" + key + "] updated to: " + value);
    }

    // ---- DASHBOARD STATS ----

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        long totalUsers = userRepo.count();
        long blockedUsers = userRepo.findAll().stream()
                .filter(u -> "BLOCKED".equalsIgnoreCase(u.getStatus())).count();
        long totalStocks = stockRepo.count();
        String marketTrend = configRepo.findById("global_market_trend")
                .map(SystemConfig::getConfigValue).orElse("NORMAL");
        String goldPrice = configRepo.findById("gold_price")
                .map(SystemConfig::getConfigValue).orElse("7200.0");

        return ResponseEntity.ok(Map.of(
                "totalUsers", totalUsers,
                "activeUsers", totalUsers - blockedUsers,
                "blockedUsers", blockedUsers,
                "totalStocks", totalStocks,
                "marketTrend", marketTrend,
                "goldPrice", goldPrice
        ));
    }
}
