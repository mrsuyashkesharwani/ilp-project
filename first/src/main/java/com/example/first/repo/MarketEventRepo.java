package com.example.first.repo;

import com.example.first.entity.MarketEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketEventRepo extends JpaRepository<MarketEvent, Long> {
}
