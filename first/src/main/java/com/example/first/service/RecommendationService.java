package com.example.first.service;

import com.example.first.Dto.RecommendationDto;
import com.example.first.entity.*;
import com.example.first.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final List<String> INCOME_CATS = List.of(
        "Salary", "Freelance", "Business", "Rental", "Other", "Stock_Sale"
    );

    private final UserRepo userRepo;
    private final BuyStockRepo buyStockRepo;
    private final GoldRepo goldRepo;
    private final GoalRepo goalRepo;

    @Transactional
    public List<RecommendationDto> getRecommendations(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<RecommendationDto> recs = new ArrayList<>();

        double income = user.getExpenses().stream()
            .filter(e -> INCOME_CATS.contains(e.getCategory()))
            .mapToDouble(Expense::getAmount).sum();
        double expenses = user.getExpenses().stream()
            .filter(e -> !INCOME_CATS.contains(e.getCategory()))
            .mapToDouble(Expense::getAmount).sum();

        List<BuyStock> stockHoldings = buyStockRepo.findActiveByUserId(userId);
        List<Gold> goldHoldings = goldRepo.findByUserUserId(userId);
        List<Goal> goals = goalRepo.findByUserUserId(userId);

        double savingsRate = income == 0 ? 0 : ((income - expenses) / income) * 100;
        double spendingRate = income == 0 ? 100 : (expenses / income) * 100;

        // Rule 1: Overspending
        if (spendingRate > 70) {
            recs.add(new RecommendationDto("EXPENSE", "Spending Too Much",
                "You are spending " + Math.round(spendingRate) + "% of your income. Aim to keep expenses below 70%.",
                "HIGH", "warning"));
        }

        // Rule 2: Low savings
        if (savingsRate < 10 && income > 0) {
            recs.add(new RecommendationDto("SAVINGS", "Increase Your Savings",
                "Your savings rate is " + Math.round(savingsRate) + "%. Try to save at least 20% of your income.",
                "HIGH", "piggy-bank"));
        }

        // Rule 3: No investments
        if (stockHoldings.isEmpty() && goldHoldings.isEmpty()) {
            recs.add(new RecommendationDto("INVESTMENT", "Start Investing",
                "You have no investments. Consider starting with low-risk stocks or digital gold to grow your wealth.",
                "MEDIUM", "trending-up"));
        }

        // Rule 4: All high-risk investments
        if (!stockHoldings.isEmpty()) {
            long highRisk = stockHoldings.stream()
                .filter(b -> "HIGH".equals(b.getStock().getRiskLevel())).count();
            if (highRisk == stockHoldings.size() && stockHoldings.size() > 1) {
                recs.add(new RecommendationDto("RISK", "Diversify Your Portfolio",
                    "All your stocks are high-risk. Consider adding some low or medium risk stocks for stability.",
                    "HIGH", "shield"));
            }
        }

        // Rule 5: No gold
        if (goldHoldings.isEmpty() && !stockHoldings.isEmpty()) {
            recs.add(new RecommendationDto("INVESTMENT", "Add Gold to Portfolio",
                "Digital gold is a safe hedge against market volatility. Consider adding gold to your portfolio.",
                "LOW", "star"));
        }

        // Rule 6: Goal close to completion
        goals.stream()
            .filter(g -> !"Completed".equals(g.getStatus()) && g.getTargetAmount() > 0)
            .filter(g -> {
                double pct = (g.getSavedAmount() != null ? g.getSavedAmount() : 0) / g.getTargetAmount() * 100;
                return pct >= 80 && pct < 100;
            })
            .forEach(g -> {
                double remaining = g.getTargetAmount() - (g.getSavedAmount() != null ? g.getSavedAmount() : 0);
                recs.add(new RecommendationDto("GOAL", "Almost There!",
                    "You are close to completing your goal '" + g.getName() + "'! Only ₹" +
                        Math.round(remaining) + " more to go.",
                    "MEDIUM", "target"));
            });

        // Rule 7: No goals set
        if (goals.isEmpty()) {
            recs.add(new RecommendationDto("GOAL", "Set Financial Goals",
                "Setting financial goals helps you stay focused. Create a goal for savings, vacation, or emergency fund.",
                "LOW", "flag"));
        }

        // Rule 8: Good savings - positive reinforcement
        if (savingsRate >= 20) {
            recs.add(new RecommendationDto("SAVINGS", "Great Savings Habit!",
                "You're saving " + Math.round(savingsRate) + "% of your income. Keep it up!",
                "LOW", "check-circle"));
        }

        return recs;
    }
}
