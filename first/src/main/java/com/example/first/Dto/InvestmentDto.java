package com.example.first.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentDto {


    private String stockName;

    private Double investedAmount;

    private int quantity;

    private Double riskPercent;

    private LocalDate investmentDate;

    // User relation id
    private Long userId;

}
