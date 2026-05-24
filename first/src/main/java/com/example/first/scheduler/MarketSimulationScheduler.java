package com.example.first.scheduler;

import com.example.first.entity.*;
import com.example.first.repo.*;
import com.example.first.websocket.MarketWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarketSimulationScheduler {

    private final StockRepo stockRepo;
    private final StockHistoryRepo stockHistoryRepo;
    private final GoldPriceRepo goldPriceRepo;
    private final GoldHistoryRepo goldHistoryRepo;
    private final MarketEventRepo marketEventRepo;
    private final MarketWebSocketHandler webSocketHandler;

    private final Random random = new Random();

    @Scheduled(fixedDelay = 30000) // every 30 seconds
    @Transactional
    public void updateStockPrices() {
        List<Stock> stocks = stockRepo.findByStockStatus("OPEN");
        List<MarketEvent> activeEvents = marketEventRepo.findByActive(true);

        for (Stock stock : stocks) {
            double oldPrice = stock.getCurrentPrice();

            // Base fluctuation based on risk level
            double maxFluctuation = switch (stock.getRiskLevel()) {
                case "LOW"    -> 0.01;
                case "MEDIUM" -> 0.03;
                case "HIGH"   -> 0.07;
                default       -> 0.02;
            };

            // Random movement direction with slight bull bias (55/45)
            double direction = random.nextDouble() < 0.55 ? 1.0 : -1.0;
            double fluctuationPct = random.nextDouble() * maxFluctuation * direction;

            // Apply active market events
            for (MarketEvent event : activeEvents) {
                if ("ALL".equals(event.getSector()) || event.getSector().equals(stock.getSector())) {
                    double eventEffect = (event.getMagnitude() / 100.0) * 0.3; // partial event effect
                    if ("BULL".equals(event.getMarketEffect())) {
                        fluctuationPct += eventEffect * random.nextDouble();
                    } else {
                        fluctuationPct -= eventEffect * random.nextDouble();
                    }
                }
            }

            double newPrice = oldPrice * (1 + fluctuationPct);
            newPrice = Math.max(newPrice, oldPrice * 0.8); // floor: no more than 20% drop
            newPrice = Math.round(newPrice * 100.0) / 100.0;

            stock.setPreviousPrice(oldPrice);
            stock.setCurrentPrice(newPrice);
            stock.setLastUpdated(LocalDateTime.now());

            // Update daily high/low
            if (stock.getDailyHigh() == null || newPrice > stock.getDailyHigh()) stock.setDailyHigh(newPrice);
            if (stock.getDailyLow() == null || newPrice < stock.getDailyLow())   stock.setDailyLow(newPrice);

            // Update trend
            double changePercent = ((newPrice - oldPrice) / oldPrice) * 100;
            stock.setMarketTrend(changePercent > 0.5 ? "BULL" : changePercent < -0.5 ? "BEAR" : "NEUTRAL");

            stockRepo.save(stock);

            // Save history
            StockHistory history = new StockHistory();
            history.setStock(stock);
            history.setOldPrice(oldPrice);
            history.setNewPrice(newPrice);
            history.setUpdatedAt(LocalDateTime.now());
            stockHistoryRepo.save(history);
        }

        // Broadcast to WebSocket clients
        try {
            String items = stocks.stream().map(s ->
                String.format("{\"type\":\"STOCK_UPDATE\",\"stockId\":%d,\"companyName\":\"%s\",\"currentPrice\":%.2f,\"previousPrice\":%.2f,\"trend\":\"%s\"}",
                    s.getStockId(), s.getCompanyName(), s.getCurrentPrice(),
                    s.getPreviousPrice() != null ? s.getPreviousPrice() : s.getCurrentPrice(),
                    s.getMarketTrend() != null ? s.getMarketTrend() : "NEUTRAL")
            ).collect(Collectors.joining(","));
            webSocketHandler.broadcast("{\"type\":\"MARKET_UPDATE\",\"data\":[" + items + "]}");
        } catch (Exception e) {
            log.warn("WebSocket broadcast error: {}", e.getMessage());
        }

        log.info("Stock prices updated for {} stocks", stocks.size());
    }

    @Scheduled(fixedDelay = 60000) // every 60 seconds
    @Transactional
    public void updateGoldPrice() {
        GoldPrice gp = goldPriceRepo.findTopByOrderByPriceIdDesc().orElse(null);
        if (gp == null) return;

        double oldPrice = gp.getCurrentPrice();
        // Gold: ±0.5% to ±2% fluctuation
        double maxFluctuation = 0.02;
        double minFluctuation = 0.005;
        double fluctuation = (minFluctuation + random.nextDouble() * (maxFluctuation - minFluctuation));
        double direction = random.nextDouble() < 0.52 ? 1.0 : -1.0; // slight bull bias
        double newPrice = oldPrice * (1 + fluctuation * direction);
        newPrice = Math.round(newPrice * 100.0) / 100.0;

        gp.setPreviousPrice(oldPrice);
        gp.setCurrentPrice(newPrice);
        gp.setLastUpdated(LocalDateTime.now());
        goldPriceRepo.save(gp);

        GoldHistory history = new GoldHistory();
        history.setOldPrice(oldPrice);
        history.setNewPrice(newPrice);
        history.setUpdatedAt(LocalDateTime.now());
        goldHistoryRepo.save(history);

        try {
            webSocketHandler.broadcast(String.format(
                "{\"type\":\"GOLD_UPDATE\",\"currentPrice\":%.2f,\"previousPrice\":%.2f,\"trend\":\"%s\"}",
                newPrice, oldPrice, newPrice > oldPrice ? "UP" : "DOWN"));
        } catch (Exception e) {
            log.warn("Gold WebSocket broadcast error: {}", e.getMessage());
        }

        log.info("Gold price updated: {} -> {}", oldPrice, newPrice);
    }
}
