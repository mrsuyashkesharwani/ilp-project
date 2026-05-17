package com.example.first.repo;

import com.example.first.entity.Gold;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GoldRepo extends JpaRepository<Gold, Long> {
    List<Gold> findByUserUserId(Long userId);
}
