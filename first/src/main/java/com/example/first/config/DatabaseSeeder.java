package com.example.first.config;

import com.example.first.entity.Stock;
import com.example.first.entity.SystemConfig;
import com.example.first.entity.User;
import com.example.first.repo.StockRepo;
import com.example.first.repo.SystemConfigRepo;
import com.example.first.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepo userRepo;
    private final StockRepo stockRepo;
    private final SystemConfigRepo configRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedSystemConfigs();
        seedAdminUser();
        seedStocks();
    }

    private void seedSystemConfigs() {
        createConfigIfAbsent("low_risk_threshold", "1.5");
        createConfigIfAbsent("medium_risk_threshold", "3.5");
        createConfigIfAbsent("high_risk_threshold", "7.5");
        createConfigIfAbsent("global_market_trend", "NORMAL");
        createConfigIfAbsent("gold_price", "7200.0");
    }

    private void createConfigIfAbsent(String key, String defaultValue) {
        if (!configRepo.existsById(key)) {
            configRepo.save(new SystemConfig(key, defaultValue));
        }
    }

    private void seedAdminUser() {
        if (userRepo.findByEmail("admin@fintech.com") == null) {
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@fintech.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setMobileNo("9999999999");
            admin.setRole("ROLE_ADMIN");
            admin.setWalletBalance(100000.0);
            admin.setStatus("ACTIVE");
            userRepo.save(admin);
        }
    }

    private void seedStocks() {
        if (stockRepo.count() == 0) {
            List<Stock> stocks = List.of(
                    // Low Risk
                    new Stock(null, "Reliance Industries", "RELIANCE", 2400.0, 2400.0, "LOW", 1.2, 12.0, 15000000.0, "Energy", 1.2, "OPEN", LocalDateTime.now()),
                    new Stock(null, "HDFC Bank", "HDFCBANK", 1600.0, 1600.0, "LOW", 1.0, 10.0, 12000000.0, "Finance", 1.0, "OPEN", LocalDateTime.now()),
                    new Stock(null, "State Bank of India", "SBIN", 750.0, 750.0, "LOW", 1.5, 11.0, 600000.0, "Finance", 1.5, "OPEN", LocalDateTime.now()),

                    // Medium Risk
                    new Stock(null, "TCS", "TCS", 3900.0, 3900.0, "MEDIUM", 3.2, 15.0, 14000000.0, "IT", 3.2, "OPEN", LocalDateTime.now()),
                    new Stock(null, "Infosys", "INFY", 1450.0, 1450.0, "MEDIUM", 3.5, 16.0, 6000000.0, "IT", 3.5, "OPEN", LocalDateTime.now()),
                    new Stock(null, "Tata Motors", "TATAMOTORS", 950.0, 950.0, "MEDIUM", 4.0, 18.0, 3000000.0, "Automotive", 4.0, "OPEN", LocalDateTime.now()),

                    // High Risk
                    new Stock(null, "Zomato", "ZOMATO", 180.0, 180.0, "HIGH", 7.5, 25.0, 150000.0, "Tech", 7.5, "OPEN", LocalDateTime.now()),
                    new Stock(null, "Adani Ports", "ADANIPORTS", 1300.0, 1300.0, "HIGH", 7.2, 22.0, 2800000.0, "Infrastructure", 7.2, "OPEN", LocalDateTime.now()),
                    new Stock(null, "Wipro", "WIPRO", 480.0, 480.0, "HIGH", 6.8, 20.0, 2500000.0, "IT", 6.8, "OPEN", LocalDateTime.now())
            );
            stockRepo.saveAll(stocks);
        }
    }
}
