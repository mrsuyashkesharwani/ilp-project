package com.example.first.entity;
// ==========================
// USER ENTITY
// ==========================



import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    @Column(unique = true)
    private String email;

    private String mobileNo;

    private String password;

    private String role; // ROLE_USER or ROLE_ADMIN

    private Double walletBalance = 0.0;

    private String status; // ACTIVE or BLOCKED

    // ================= RELATION =================

    @JsonIgnore
    @OneToMany(mappedBy = "usertemp", cascade = CascadeType.ALL,
               fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Expense> expenses = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "usertemp", cascade = CascadeType.ALL,
               fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Investment> investments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
               fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Gold> goldHoldings = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
               fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Goal> goals = new ArrayList<>();
//
//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
//    private FinancialProfile financialProfile;
//
//    @OneToMany(mappedBy = "usertemp", cascade = CascadeType.ALL)
//    private List<Stock> Stocks;

    // Getter Setter
}
