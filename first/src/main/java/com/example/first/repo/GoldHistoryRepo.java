package com.example.first.repo;

import com.example.first.entity.GoldHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoldHistoryRepo extends JpaRepository<GoldHistory, Long> {
}
