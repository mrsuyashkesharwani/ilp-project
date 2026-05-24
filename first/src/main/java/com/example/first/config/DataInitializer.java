package com.example.first.config;

import com.example.first.entity.GoldPrice;
import com.example.first.entity.MarketEvent;
import com.example.first.entity.Stock;
import com.example.first.repo.GoldPriceRepo;
import com.example.first.repo.MarketEventRepo;
import com.example.first.repo.StockRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final StockRepo stockRepo;
    private final GoldPriceRepo goldPriceRepo;
    private final MarketEventRepo marketEventRepo;

    @Override
    public void run(String... args) {
        seedStocks();
        seedGoldPrice();
        seedMarketEvents();
    }

    private void seedStocks() {
        if (stockRepo.count() != 0) return;

        LocalDateTime now = LocalDateTime.now();

        Stock s1 = new Stock();
        s1.setCompanyName("TCS");
        s1.setCurrentPrice(3500.0);
        s1.setPreviousPrice(3480.0);
        s1.setRiskLevel("LOW");
        s1.setRiskPercent(1.0);
        s1.setExpectedReturn(12.0);
        s1.setMarketCap(1280000.0);
        s1.setSector("TECHNOLOGY");
        s1.setVolatility(1.5);
        s1.setStockStatus("OPEN");
        s1.setDailyHigh(3520.0);
        s1.setDailyLow(3470.0);
        s1.setMarketTrend("BULL");
        s1.setLastUpdated(now);
        stockRepo.save(s1);

        Stock s2 = new Stock();
        s2.setCompanyName("Infosys");
        s2.setCurrentPrice(1800.0);
        s2.setPreviousPrice(1780.0);
        s2.setRiskLevel("LOW");
        s2.setRiskPercent(1.0);
        s2.setExpectedReturn(11.0);
        s2.setMarketCap(755000.0);
        s2.setSector("TECHNOLOGY");
        s2.setVolatility(1.5);
        s2.setStockStatus("OPEN");
        s2.setDailyHigh(1820.0);
        s2.setDailyLow(1770.0);
        s2.setMarketTrend("NEUTRAL");
        s2.setLastUpdated(now);
        stockRepo.save(s2);

        Stock s3 = new Stock();
        s3.setCompanyName("Reliance");
        s3.setCurrentPrice(2800.0);
        s3.setPreviousPrice(2760.0);
        s3.setRiskLevel("MEDIUM");
        s3.setRiskPercent(3.0);
        s3.setExpectedReturn(15.0);
        s3.setMarketCap(1900000.0);
        s3.setSector("ENERGY");
        s3.setVolatility(2.5);
        s3.setStockStatus("OPEN");
        s3.setDailyHigh(2840.0);
        s3.setDailyLow(2730.0);
        s3.setMarketTrend("BULL");
        s3.setLastUpdated(now);
        stockRepo.save(s3);

        Stock s4 = new Stock();
        s4.setCompanyName("HDFC");
        s4.setCurrentPrice(1650.0);
        s4.setPreviousPrice(1630.0);
        s4.setRiskLevel("MEDIUM");
        s4.setRiskPercent(2.5);
        s4.setExpectedReturn(13.0);
        s4.setMarketCap(920000.0);
        s4.setSector("BANKING");
        s4.setVolatility(2.0);
        s4.setStockStatus("OPEN");
        s4.setDailyHigh(1670.0);
        s4.setDailyLow(1620.0);
        s4.setMarketTrend("NEUTRAL");
        s4.setLastUpdated(now);
        stockRepo.save(s4);

        Stock s5 = new Stock();
        s5.setCompanyName("Wipro");
        s5.setCurrentPrice(480.0);
        s5.setPreviousPrice(470.0);
        s5.setRiskLevel("MEDIUM");
        s5.setRiskPercent(2.0);
        s5.setExpectedReturn(10.0);
        s5.setMarketCap(250000.0);
        s5.setSector("TECHNOLOGY");
        s5.setVolatility(2.0);
        s5.setStockStatus("OPEN");
        s5.setDailyHigh(490.0);
        s5.setDailyLow(465.0);
        s5.setMarketTrend("BULL");
        s5.setLastUpdated(now);
        stockRepo.save(s5);

        Stock s6 = new Stock();
        s6.setCompanyName("SBI");
        s6.setCurrentPrice(820.0);
        s6.setPreviousPrice(800.0);
        s6.setRiskLevel("HIGH");
        s6.setRiskPercent(5.0);
        s6.setExpectedReturn(18.0);
        s6.setMarketCap(730000.0);
        s6.setSector("BANKING");
        s6.setVolatility(3.0);
        s6.setStockStatus("OPEN");
        s6.setDailyHigh(840.0);
        s6.setDailyLow(795.0);
        s6.setMarketTrend("BEAR");
        s6.setLastUpdated(now);
        stockRepo.save(s6);

        Stock s7 = new Stock();
        s7.setCompanyName("Tata Motors");
        s7.setCurrentPrice(950.0);
        s7.setPreviousPrice(920.0);
        s7.setRiskLevel("HIGH");
        s7.setRiskPercent(6.0);
        s7.setExpectedReturn(20.0);
        s7.setMarketCap(350000.0);
        s7.setSector("AUTOMOBILE");
        s7.setVolatility(4.0);
        s7.setStockStatus("OPEN");
        s7.setDailyHigh(970.0);
        s7.setDailyLow(910.0);
        s7.setMarketTrend("BULL");
        s7.setLastUpdated(now);
        stockRepo.save(s7);

        Stock s8 = new Stock();
        s8.setCompanyName("ITC");
        s8.setCurrentPrice(450.0);
        s8.setPreviousPrice(445.0);
        s8.setRiskLevel("LOW");
        s8.setRiskPercent(1.0);
        s8.setExpectedReturn(9.0);
        s8.setMarketCap(560000.0);
        s8.setSector("FMCG");
        s8.setVolatility(1.0);
        s8.setStockStatus("OPEN");
        s8.setDailyHigh(455.0);
        s8.setDailyLow(440.0);
        s8.setMarketTrend("NEUTRAL");
        s8.setLastUpdated(now);
        stockRepo.save(s8);

        Stock s9 = new Stock();
        s9.setCompanyName("Zomato");
        s9.setCurrentPrice(220.0);
        s9.setPreviousPrice(210.0);
        s9.setRiskLevel("HIGH");
        s9.setRiskPercent(7.0);
        s9.setExpectedReturn(25.0);
        s9.setMarketCap(195000.0);
        s9.setSector("FOOD_TECH");
        s9.setVolatility(5.0);
        s9.setStockStatus("OPEN");
        s9.setDailyHigh(230.0);
        s9.setDailyLow(205.0);
        s9.setMarketTrend("BULL");
        s9.setLastUpdated(now);
        stockRepo.save(s9);

        Stock s10 = new Stock();
        s10.setCompanyName("Adani Ports");
        s10.setCurrentPrice(1200.0);
        s10.setPreviousPrice(1180.0);
        s10.setRiskLevel("HIGH");
        s10.setRiskPercent(5.5);
        s10.setExpectedReturn(22.0);
        s10.setMarketCap(260000.0);
        s10.setSector("INFRASTRUCTURE");
        s10.setVolatility(3.5);
        s10.setStockStatus("OPEN");
        s10.setDailyHigh(1220.0);
        s10.setDailyLow(1170.0);
        s10.setMarketTrend("BEAR");
        s10.setLastUpdated(now);
        stockRepo.save(s10);
    }

    private void seedGoldPrice() {
        if (goldPriceRepo.count() != 0) return;

        GoldPrice gp = new GoldPrice();
        gp.setCurrentPrice(7500.0);
        gp.setPreviousPrice(7450.0);
        gp.setLastUpdated(LocalDateTime.now());
        goldPriceRepo.save(gp);
    }

    private void seedMarketEvents() {
        if (marketEventRepo.count() != 0) return;

        LocalDateTime now = LocalDateTime.now();

        MarketEvent e1 = new MarketEvent();
        e1.setEventName("IT Sector Boom");
        e1.setDescription("Technology stocks surge on strong earnings");
        e1.setMarketEffect("BULL");
        e1.setMagnitude(2.0);
        e1.setSector("TECHNOLOGY");
        e1.setActive(true);
        e1.setCreatedAt(now);
        marketEventRepo.save(e1);

        MarketEvent e2 = new MarketEvent();
        e2.setEventName("Banking Sector Pressure");
        e2.setDescription("Rising NPAs concern banking sector");
        e2.setMarketEffect("BEAR");
        e2.setMagnitude(2.5);
        e2.setSector("BANKING");
        e2.setActive(false);
        e2.setCreatedAt(now);
        marketEventRepo.save(e2);

        MarketEvent e3 = new MarketEvent();
        e3.setEventName("Gold Price Rally");
        e3.setDescription("Gold prices surge amid global uncertainty");
        e3.setMarketEffect("BULL");
        e3.setMagnitude(1.5);
        e3.setSector("ALL");
        e3.setActive(false);
        e3.setCreatedAt(now);
        marketEventRepo.save(e3);

        MarketEvent e4 = new MarketEvent();
        e4.setEventName("Market Correction");
        e4.setDescription("Market sees broad correction");
        e4.setMarketEffect("BEAR");
        e4.setMagnitude(3.0);
        e4.setSector("ALL");
        e4.setActive(false);
        e4.setCreatedAt(now);
        marketEventRepo.save(e4);

        MarketEvent e5 = new MarketEvent();
        e5.setEventName("Economic Growth Boost");
        e5.setDescription("GDP growth exceeds estimates");
        e5.setMarketEffect("BULL");
        e5.setMagnitude(2.0);
        e5.setSector("ALL");
        e5.setActive(true);
        e5.setCreatedAt(now);
        marketEventRepo.save(e5);
    }
}
