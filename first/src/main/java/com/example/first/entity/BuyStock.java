package com.example.first.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "buy_stock")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class BuyStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long buyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    private Integer quantity;
    private Double buyPrice;        // price per unit at time of purchase
    private Double totalAmount;     // quantity * buyPrice
    private LocalDateTime purchaseDate;
}
