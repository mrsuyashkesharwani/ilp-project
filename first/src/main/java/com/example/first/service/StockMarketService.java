package com.example.first.service;

import com.example.first.Dto.*;
import com.example.first.entity.*;
import com.example.first.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockMarketService {

    private final StockRepo stockRepo;
    private final BuyStockRepo buyStockRepo;
    private final StockHistoryRepo stockHistoryRepo;
    private final UserRepo userRepo;
    private final ExpenseRepo expenseRepo;

    public List<StockDto> getAllStocks() {
        return stockRepo.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public StockDto getStockById(Long stockId) {
        Stock stock = stockRepo.findById(stockId)
            .orElseThrow(() -> new RuntimeException("Stock not found: " + stockId));
        return toDto(stock);
    }

    public List<StockDto> getStocksByRisk(String riskLevel) {
        return stockRepo.findByRiskLevel(riskLevel.toUpperCase()).stream()
            .map(this::toDto).collect(Collectors.toList());
    }

    public List<StockDto> getTopGainers() {
        return stockRepo.findTopGainers().stream()
            .limit(5).map(this::toDto).collect(Collectors.toList());
    }

    public List<StockDto> getTopLosers() {
        return stockRepo.findTopLosers().stream()
            .limit(5).map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public PortfolioItemDto buyStock(BuyStockRequestDto req) {
        User user = userRepo.findById(req.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        Stock stock = stockRepo.findById(req.getStockId())
            .orElseThrow(() -> new RuntimeException("Stock not found"));

        if (!"OPEN".equals(stock.getStockStatus())) {
            throw new RuntimeException("Market is closed");
        }

        double totalAmount = stock.getCurrentPrice() * req.getQuantity();

        BuyStock buyStock = new BuyStock();
        buyStock.setUser(user);
        buyStock.setStock(stock);
        buyStock.setQuantity(req.getQuantity());
        buyStock.setBuyPrice(stock.getCurrentPrice());
        buyStock.setTotalAmount(totalAmount);
        buyStock.setPurchaseDate(LocalDateTime.now());
        BuyStock saved = buyStockRepo.save(buyStock);

        // Record as expense
        Expense exp = new Expense();
        exp.setTitle("Buy " + stock.getCompanyName() + " x" + req.getQuantity());
        exp.setAmount(totalAmount);
        exp.setCategory("Stock_Buy");
        exp.setExpenseDate(java.time.LocalDate.now());
        exp.setUsertemp(user);
        expenseRepo.save(exp);

        return toPortfolioItem(saved);
    }

    @Transactional
    public PortfolioItemDto sellStock(SellStockRequestDto req) {
        BuyStock buyStock = buyStockRepo.findById(req.getBuyStockId())
            .orElseThrow(() -> new RuntimeException("Stock holding not found"));

        if (!buyStock.getUser().getUserId().equals(req.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }
        if (req.getQuantity() > buyStock.getQuantity()) {
            throw new RuntimeException("Cannot sell more than owned: " + buyStock.getQuantity());
        }

        Stock stock = buyStock.getStock();
        double saleAmount = stock.getCurrentPrice() * req.getQuantity();

        // Record sale as income
        Expense exp = new Expense();
        exp.setTitle("Sell " + stock.getCompanyName() + " x" + req.getQuantity());
        exp.setAmount(saleAmount);
        exp.setCategory("Stock_Sale");
        exp.setExpenseDate(java.time.LocalDate.now());
        exp.setUsertemp(buyStock.getUser());
        expenseRepo.save(exp);

        if (req.getQuantity().equals(buyStock.getQuantity())) {
            buyStockRepo.delete(buyStock);
            return null;
        } else {
            buyStock.setQuantity(buyStock.getQuantity() - req.getQuantity());
            buyStock.setTotalAmount(buyStock.getBuyPrice() * buyStock.getQuantity());
            BuyStock updatedBuyStock = buyStockRepo.save(buyStock);
            return toPortfolioItem(updatedBuyStock);
        }
    }

    public List<PortfolioItemDto> getUserPortfolio(Long userId) {
        return buyStockRepo.findActiveByUserId(userId).stream()
            .map(this::toPortfolioItem)
            .collect(Collectors.toList());
    }

    public List<StockHistory> getStockHistory(Long stockId) {
        return stockHistoryRepo.findTop20ByStockStockIdOrderByUpdatedAtDesc(stockId);
    }

    public StockDto toDto(Stock s) {
        StockDto dto = new StockDto();
        dto.setStockId(s.getStockId());
        dto.setCompanyName(s.getCompanyName());
        dto.setCurrentPrice(s.getCurrentPrice());
        dto.setPreviousPrice(s.getPreviousPrice());
        dto.setRiskLevel(s.getRiskLevel());
        dto.setRiskPercent(s.getRiskPercent());
        dto.setExpectedReturn(s.getExpectedReturn());
        dto.setMarketCap(s.getMarketCap());
        dto.setSector(s.getSector());
        dto.setVolatility(s.getVolatility());
        dto.setStockStatus(s.getStockStatus());
        dto.setDailyHigh(s.getDailyHigh());
        dto.setDailyLow(s.getDailyLow());
        dto.setMarketTrend(s.getMarketTrend());
        dto.setLastUpdated(s.getLastUpdated());
        if (s.getPreviousPrice() != null && s.getPreviousPrice() != 0) {
            double change = s.getCurrentPrice() - s.getPreviousPrice();
            dto.setChangeAmount(Math.round(change * 100.0) / 100.0);
            dto.setChangePercent(Math.round((change / s.getPreviousPrice() * 100) * 100.0) / 100.0);
        }
        return dto;
    }

    public PortfolioItemDto toPortfolioItem(BuyStock b) {
        Stock s = b.getStock();
        double currentValue = s.getCurrentPrice() * b.getQuantity();
        double totalInvestment = b.getBuyPrice() * b.getQuantity();
        double pl = currentValue - totalInvestment;
        double plPct = totalInvestment == 0 ? 0 : (pl / totalInvestment) * 100;

        return new PortfolioItemDto(
            b.getBuyId(), s.getStockId(), s.getCompanyName(), s.getSector(),
            s.getRiskLevel(), b.getQuantity(), b.getBuyPrice(), s.getCurrentPrice(),
            Math.round(totalInvestment * 100.0) / 100.0,
            Math.round(currentValue * 100.0) / 100.0,
            Math.round(pl * 100.0) / 100.0,
            Math.round(plPct * 100.0) / 100.0
        );
    }
}
