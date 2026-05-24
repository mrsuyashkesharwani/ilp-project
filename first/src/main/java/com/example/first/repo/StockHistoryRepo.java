package com.example.first.repo;

import com.example.first.entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockHistoryRepo extends JpaRepository<StockHistory, Long> {
}
