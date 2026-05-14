package com.example.first.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long investmentId;

    private String stockName;

    private Double investedAmount;

    private int quantity;

    private Double riskPercent;

    private LocalDate investmentDate;

    // ================= RELATION =================

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User usertemp;

}
