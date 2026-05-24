package com.example.first.repo;

import com.example.first.entity.GoldPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GoldPriceRepo extends JpaRepository<GoldPrice, Long> {
    Optional<GoldPrice> findTopByOrderByPriceIdDesc();
}
