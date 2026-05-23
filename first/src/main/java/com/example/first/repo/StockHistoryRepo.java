package com.example.first.repo;

import com.example.first.entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockHistoryRepo extends JpaRepository<StockHistory, Long> {
    List<StockHistory> findByStockStockIdOrderByUpdatedAtDesc(Long stockId);
    List<StockHistory> findTop20ByStockStockIdOrderByUpdatedAtDesc(Long stockId);
}
