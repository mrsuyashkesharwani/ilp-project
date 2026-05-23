package com.example.first.repo;

import com.example.first.entity.GoldHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GoldHistoryRepo extends JpaRepository<GoldHistory, Long> {
    List<GoldHistory> findTop30ByOrderByUpdatedAtDesc();
}
