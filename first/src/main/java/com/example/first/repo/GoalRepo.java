package com.example.first.repo;

import com.example.first.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GoalRepo extends JpaRepository<Goal, Long> {
    List<Goal> findByUserUserId(Long userId);
}
