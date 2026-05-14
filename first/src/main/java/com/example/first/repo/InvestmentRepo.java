package com.example.first.repo;


import com.example.first.entity.Investment;


import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentRepo  extends JpaRepository<Investment, Long>{



}
