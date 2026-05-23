package com.example.first.repo;

import com.example.first.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface StockRepo extends JpaRepository<Stock, Long> {
    List<Stock> findByRiskLevel(String riskLevel);
    List<Stock> findBySector(String sector);

    @Query("SELECT s FROM Stock s ORDER BY (s.currentPrice - s.previousPrice) / s.previousPrice DESC")
    List<Stock> findTopGainers();

    @Query("SELECT s FROM Stock s ORDER BY (s.currentPrice - s.previousPrice) / s.previousPrice ASC")
    List<Stock> findTopLosers();

    List<Stock> findByStockStatus(String status);
}
