package com.example.first.service;

import com.example.first.Dto.*;
import com.example.first.entity.*;
import com.example.first.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialWellnessService {

    private static final List<String> INCOME_CATS = List.of(
        "Salary", "Freelance", "Business", "Rental", "Other", "Stock_Sale"
    );
    private static final List<String> INVEST_CATS = List.of(
        "Stock_Buy", "Stock_Credited", "gold", "invest"
    );

    private final UserRepo userRepo;
    private final InvestmentRepo investmentRepo;
    private final BuyStockRepo buyStockRepo;
    private final GoldRepo goldRepo;
    private final GoalRepo goalRepo;

    @Transactional
    public WellnessDto computeWellness(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        double totalIncome = user.getExpenses().stream()
            .filter(e -> INCOME_CATS.contains(e.getCategory()))
            .mapToDouble(Expense::getAmount).sum();

        double totalExpenses = user.getExpenses().stream()
            .filter(e -> !INCOME_CATS.contains(e.getCategory()) && !isInvestmentCategory(e.getCategory()))
            .mapToDouble(Expense::getAmount).sum();

        double totalInvested = user.getInvestments().stream()
            .mapToDouble(Investment::getInvestedAmount).sum();

        // Stock portfolio value
        List<BuyStock> stockHoldings = buyStockRepo.findActiveByUserId(userId);
        double stockCurrentValue = stockHoldings.stream()
            .mapToDouble(b -> b.getStock().getCurrentPrice() * b.getQuantity()).sum();
        double stockInvestment = stockHoldings.stream()
            .mapToDouble(b -> b.getBuyPrice() * b.getQuantity()).sum();

        // Gold value
        double goldCurrentValue = goldRepo.findByUserUserId(userId).stream()
            .mapToDouble(g -> g.getQuantityGrams() * g.getCurrentPricePerGram()).sum();
        double goldInvestment = goldRepo.findByUserUserId(userId).stream()
            .mapToDouble(g -> g.getQuantityGrams() * g.getPurchasePricePerGram()).sum();

        double totalSaved = totalIncome - totalExpenses;
        double savingsRate = totalIncome == 0 ? 0 : (totalSaved / totalIncome) * 100;
        double investmentRate = totalIncome == 0 ? 0 : ((stockInvestment + goldInvestment + totalInvested) / totalIncome) * 100;

        // Goals
        List<Goal> goals = goalRepo.findByUserUserId(userId);
        int completedGoals = (int) goals.stream().filter(g -> "Completed".equals(g.getStatus())).count();
        int goalCompletionRate = goals.isEmpty() ? 0 : (int) ((completedGoals * 100.0) / goals.size());

        // Risk
        RiskAnalysisResult risk = computeRisk(userId, stockHoldings);

        // Score calculation
        int score = 40; // base
        if (savingsRate >= 20) score += 20;
        else if (savingsRate >= 10) score += 10;
        else if (savingsRate >= 5) score += 5;

        if (investmentRate >= 15) score += 20;
        else if (investmentRate >= 5) score += 10;
        else if (investmentRate > 0) score += 5;

        if (totalIncome > 0) score += 5;
        if (goalCompletionRate >= 50) score += 10;
        else if (goalCompletionRate > 0) score += 5;

        // Diversification bonus
        Set<String> sectors = stockHoldings.stream().map(b -> b.getStock().getSector()).collect(Collectors.toSet());
        double diversification = Math.min(sectors.size() * 10.0, 30.0);
        if (sectors.size() >= 3) score += 5;
        if (!goldRepo.findByUserUserId(userId).isEmpty()) score += 5; // has gold

        score = Math.min(score, 100);

        String level = score >= 80 ? "Excellent" : score >= 60 ? "Good" : score >= 40 ? "Fair" : "Needs Improvement";

        WellnessDto dto = new WellnessDto();
        dto.setScore(score);
        dto.setLevel(level);
        dto.setSavingsRate(Math.round(savingsRate * 10.0) / 10.0);
        dto.setInvestmentRate(Math.round(investmentRate * 10.0) / 10.0);
        dto.setTotalIncome(totalIncome);
        dto.setTotalExpenses(totalExpenses);
        dto.setTotalInvested(stockInvestment + goldInvestment + totalInvested);
        dto.setTotalSaved(Math.max(0, totalSaved));
        dto.setRiskScore(risk.riskScore());
        dto.setRiskLevel(risk.riskLevel());
        dto.setDiversificationScore(diversification);
        dto.setGoalCompletionRate(goalCompletionRate);
        dto.setRecommendations(new ArrayList<>());
        return dto;
    }

    private boolean isInvestmentCategory(String cat) {
        if (cat == null) return false;
        String lower = cat.toLowerCase();
        return INVEST_CATS.stream().anyMatch(k -> lower.contains(k.toLowerCase()));
    }

    private RiskAnalysisResult computeRisk(Long userId, List<BuyStock> holdings) {
        if (holdings.isEmpty()) {
            return new RiskAnalysisResult(0.0, "LOW");
        }
        double totalValue = holdings.stream().mapToDouble(b -> b.getStock().getCurrentPrice() * b.getQuantity()).sum();
        if (totalValue == 0) return new RiskAnalysisResult(0.0, "LOW");

        double weightedRisk = holdings.stream()
            .mapToDouble(b -> {
                double weight = (b.getStock().getCurrentPrice() * b.getQuantity()) / totalValue;
                return weight * b.getStock().getRiskPercent();
            }).sum();

        String level = weightedRisk < 2.0 ? "LOW" : weightedRisk < 4.0 ? "MEDIUM" : "HIGH";
        return new RiskAnalysisResult(Math.round(weightedRisk * 100.0) / 100.0, level);
    }

    private record RiskAnalysisResult(double riskScore, String riskLevel) {}
}
