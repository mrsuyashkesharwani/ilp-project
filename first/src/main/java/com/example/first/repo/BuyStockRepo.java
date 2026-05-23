package com.example.first.repo;

import com.example.first.entity.BuyStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BuyStockRepo extends JpaRepository<BuyStock, Long> {
    List<BuyStock> findByUserUserId(Long userId);
    List<BuyStock> findByStockStockId(Long stockId);
    List<BuyStock> findByUserUserIdAndStockStockId(Long userId, Long stockId);

    @Query("SELECT b FROM BuyStock b WHERE b.user.userId = :userId AND b.quantity > 0")
    List<BuyStock> findActiveByUserId(Long userId);
}
