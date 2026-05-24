package com.example.first.repo;

import com.example.first.entity.FinancialProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FinancialProfileRepo extends JpaRepository<FinancialProfile, Long> {
    Optional<FinancialProfile> findByUserUserId(Long userId);
}
