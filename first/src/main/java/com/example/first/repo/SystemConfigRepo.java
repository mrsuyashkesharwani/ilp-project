package com.example.first.repo;

import com.example.first.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigRepo extends JpaRepository<SystemConfig, String> {
}
