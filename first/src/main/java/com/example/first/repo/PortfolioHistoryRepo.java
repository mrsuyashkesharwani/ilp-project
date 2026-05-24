package com.example.first.repo;

import com.example.first.entity.PortfolioHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PortfolioHistoryRepo extends JpaRepository<PortfolioHistory, Long> {
    List<PortfolioHistory> findByUserUserIdOrderBySnapshotDateDesc(Long userId);
    List<PortfolioHistory> findTop30ByUserUserIdOrderBySnapshotDateDesc(Long userId);
}
