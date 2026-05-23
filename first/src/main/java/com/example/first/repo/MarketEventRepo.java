package com.example.first.repo;

import com.example.first.entity.MarketEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MarketEventRepo extends JpaRepository<MarketEvent, Long> {
    List<MarketEvent> findByActive(boolean active);
    List<MarketEvent> findTop10ByOrderByCreatedAtDesc();
}
