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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenseId;

    private String title;

    private Double amount;

    private String category;

    private LocalDate expenseDate;

    // ================= RELATION =================

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User usertemp;






}
