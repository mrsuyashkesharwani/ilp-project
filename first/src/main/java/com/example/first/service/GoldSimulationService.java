package com.example.first.service;

import com.example.first.Dto.*;
import com.example.first.entity.*;
import com.example.first.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoldSimulationService {

    private final GoldPriceRepo goldPriceRepo;
    private final GoldHistoryRepo goldHistoryRepo;
    private final GoldRepo goldRepo;
    private final UserRepo userRepo;

    public GoldPriceDto getCurrentGoldPrice() {
        GoldPrice gp = goldPriceRepo.findTopByOrderByPriceIdDesc()
            .orElseThrow(() -> new RuntimeException("Gold price not initialized"));

        GoldPriceDto dto = new GoldPriceDto();
        dto.setCurrentPrice(gp.getCurrentPrice());
        dto.setPreviousPrice(gp.getPreviousPrice());
        dto.setLastUpdated(gp.getLastUpdated());

        if (gp.getPreviousPrice() != null && gp.getPreviousPrice() != 0) {
            double change = gp.getCurrentPrice() - gp.getPreviousPrice();
            dto.setChangeAmount(Math.round(change * 100.0) / 100.0);
            dto.setChangePercent(Math.round((change / gp.getPreviousPrice() * 100) * 100.0) / 100.0);
            dto.setTrend(change > 0 ? "UP" : change < 0 ? "DOWN" : "STABLE");
        }
        return dto;
    }

    public List<GoldHistory> getGoldHistory() {
        return goldHistoryRepo.findTop30ByOrderByUpdatedAtDesc();
    }

    @Transactional
    public List<GoldPortfolioItemDto> getUserGoldPortfolio(Long userId) {
        GoldPrice currentPrice = goldPriceRepo.findTopByOrderByPriceIdDesc()
            .orElse(null);
        double price = currentPrice != null ? currentPrice.getCurrentPrice() : 7500.0;

        return goldRepo.findByUserUserId(userId).stream()
            .map(g -> {
                GoldPortfolioItemDto dto = new GoldPortfolioItemDto();
                dto.setGoldId(g.getGoldId());
                dto.setType(g.getType());
                dto.setQuantityGrams(g.getQuantityGrams());
                dto.setPurchasePricePerGram(g.getPurchasePricePerGram());
                dto.setCurrentPricePerGram(price);
                dto.setStorageType(g.getStorageType());
                dto.setNotes(g.getNotes());
                dto.setPurchaseDate(g.getPurchaseDate());
                dto.setUserId(userId);

                double totalInv = g.getQuantityGrams() * g.getPurchasePricePerGram();
                double curVal = g.getQuantityGrams() * price;
                double pl = curVal - totalInv;
                dto.setTotalInvestment(Math.round(totalInv * 100.0) / 100.0);
                dto.setCurrentValue(Math.round(curVal * 100.0) / 100.0);
                dto.setProfitLoss(Math.round(pl * 100.0) / 100.0);
                dto.setProfitLossPercent(totalInv == 0 ? 0 : Math.round((pl / totalInv * 100) * 100.0) / 100.0);
                return dto;
            }).collect(Collectors.toList());
    }
}
