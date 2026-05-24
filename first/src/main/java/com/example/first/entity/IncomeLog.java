package com.example.first.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "income_logs")
public class IncomeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    @SequenceGenerator(name = "transaction_seq", sequenceName = "transaction_seq", allocationSize = 1)
    private Long incomeId;

    private Double amount;
    private String source;
    private LocalDate incomeDate;
    private Double profit;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
