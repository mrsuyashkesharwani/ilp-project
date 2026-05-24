package com.example.first.scheduler;

import com.example.first.config.MarketWebSocketHandler;
import com.example.first.entity.GoldHistory;
import com.example.first.entity.Stock;
import com.example.first.entity.StockHistory;
import com.example.first.entity.SystemConfig;
import com.example.first.repo.GoldHistoryRepo;
import com.example.first.repo.StockHistoryRepo;
import com.example.first.repo.StockRepo;
import com.example.first.repo.SystemConfigRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class SimulationEngine {

    private final StockRepo stockRepo;
    private final StockHistoryRepo stockHistoryRepo;
    private final GoldHistoryRepo goldHistoryRepo;
    private final SystemConfigRepo configRepo;
    private final MarketWebSocketHandler webSocketHandler;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void simulateMarket() {
        double lowThreshold    = getDoubleConfig("low_risk_threshold", 1.5);
        double mediumThreshold = getDoubleConfig("medium_risk_threshold", 3.5);
        double highThreshold   = getDoubleConfig("high_risk_threshold", 7.5);
        String trend           = getStringConfig("global_market_trend", "NORMAL");

        List<Stock> stocks = stockRepo.findAll();
        StringBuilder stockArrayJson = new StringBuilder("[");

        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            double oldPrice = stock.getCurrentPrice();
            double limit = lowThreshold;
            if ("MEDIUM".equalsIgnoreCase(stock.getRiskCategory())) limit = mediumThreshold;
            else if ("HIGH".equalsIgnoreCase(stock.getRiskCategory())) limit = highThreshold;

            double changePercent = ThreadLocalRandom.current().nextDouble(-limit, limit);

            if ("BULL".equalsIgnoreCase(trend)) {
                changePercent += ThreadLocalRandom.current().nextDouble(0.5, 2.0);
            } else if ("BEAR".equalsIgnoreCase(trend)) {
                changePercent -= ThreadLocalRandom.current().nextDouble(0.5, 2.0);
            } else if ("CRASH".equalsIgnoreCase(trend)) {
                changePercent -= ThreadLocalRandom.current().nextDouble(4.0, 8.0);
            } else if ("BOOM".equalsIgnoreCase(trend)) {
                changePercent += ThreadLocalRandom.current().nextDouble(2.0, 5.0);
            }

            double newPrice = Math.round(Math.max(oldPrice * (1 + changePercent / 100), 1.0) * 100.0) / 100.0;
            double changePct = Math.round(((newPrice - oldPrice) / oldPrice * 100) * 100.0) / 100.0;

            stock.setPreviousPrice(oldPrice);
            stock.setCurrentPrice(newPrice);
            stock.setLastUpdated(LocalDateTime.now());
            stockRepo.save(stock);

            StockHistory history = new StockHistory();
            history.setStock(stock);
            history.setOldPrice(oldPrice);
            history.setNewPrice(newPrice);
            history.setUpdatedAt(LocalDateTime.now());
            stockHistoryRepo.save(history);

            if (i > 0) stockArrayJson.append(",");
            stockArrayJson.append("{")
                    .append("\"symbol\":\"").append(stock.getSymbol()).append("\",")
                    .append("\"companyName\":\"").append(escapeJson(stock.getCompanyName())).append("\",")
                    .append("\"currentPrice\":").append(newPrice).append(",")
                    .append("\"previousPrice\":").append(oldPrice).append(",")
                    .append("\"changePercent\":").append(changePct).append(",")
                    .append("\"riskCategory\":\"").append(stock.getRiskCategory()).append("\"")
                    .append("}");
        }
        stockArrayJson.append("]");

        // Gold simulation (safe-haven inverse correlation)
        double oldGoldPrice    = getDoubleConfig("gold_price", 7200.0);
        double goldFluctuation = ThreadLocalRandom.current().nextDouble(-0.5, 1.5);
        if ("CRASH".equalsIgnoreCase(trend)) goldFluctuation += ThreadLocalRandom.current().nextDouble(1.0, 3.0);
        else if ("BEAR".equalsIgnoreCase(trend)) goldFluctuation += ThreadLocalRandom.current().nextDouble(0.5, 1.5);

        double newGoldPrice = Math.round(oldGoldPrice * (1 + goldFluctuation / 100) * 100.0) / 100.0;
        double goldChangePct = Math.round(((newGoldPrice - oldGoldPrice) / oldGoldPrice * 100) * 100.0) / 100.0;

        SystemConfig goldConfig = configRepo.findById("gold_price")
                .orElse(new SystemConfig("gold_price", "7200.0"));
        goldConfig.setConfigValue(String.valueOf(newGoldPrice));
        configRepo.save(goldConfig);

        GoldHistory goldHistory = new GoldHistory();
        goldHistory.setOldPrice(oldGoldPrice);
        goldHistory.setNewPrice(newGoldPrice);
        goldHistory.setUpdatedAt(LocalDateTime.now());
        goldHistoryRepo.save(goldHistory);

        // Broadcast JSON built manually (no Jackson needed)
        try {
            String message = "{"
                    + "\"type\":\"MARKET_UPDATE\","
                    + "\"timestamp\":\"" + LocalDateTime.now() + "\","
                    + "\"stocks\":" + stockArrayJson + ","
                    + "\"goldPrice\":" + newGoldPrice + ","
                    + "\"goldPreviousPrice\":" + oldGoldPrice + ","
                    + "\"goldChangePercent\":" + goldChangePct
                    + "}";
            webSocketHandler.broadcast(message);
        } catch (Exception ignored) {
            // Broadcasting failure - silently ignore
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private double getDoubleConfig(String key, double defaultValue) {
        return configRepo.findById(key).map(c -> {
            try { return Double.parseDouble(c.getConfigValue()); }
            catch (Exception e) { return defaultValue; }
        }).orElse(defaultValue);
    }

    private String getStringConfig(String key, String defaultValue) {
        return configRepo.findById(key)
                .map(SystemConfig::getConfigValue)
                .orElse(defaultValue);
    }
}
