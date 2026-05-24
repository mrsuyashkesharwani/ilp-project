package com.example.first.Dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ExpanseDto {



    private Long expenseId;

    private String title;

    private Double amount;

    private String category;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;

    // User id for relation
    private Long userId;

    private Double profit;

    public ExpanseDto(Long expenseId, String title, Double amount, String category, LocalDate expenseDate, Long userId) {
        this.expenseId = expenseId;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.expenseDate = expenseDate;
        this.userId = userId;
    }

    public ExpanseDto(Long expenseId, String title, Double amount, String category, LocalDate expenseDate, Long userId, Double profit) {
        this.expenseId = expenseId;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.expenseDate = expenseDate;
        this.userId = userId;
        this.profit = profit;
    }

}
