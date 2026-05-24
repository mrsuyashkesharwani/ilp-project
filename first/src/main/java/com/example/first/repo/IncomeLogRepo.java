package com.example.first.repo;

import com.example.first.entity.IncomeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncomeLogRepo extends JpaRepository<IncomeLog, Long> {
    List<IncomeLog> findByUserUserId(Long userId);
}
