package com.example.first.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "gold")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goldId;

    private String type;           // Physical, Digital, ETF, SGB
    private Double quantityGrams;
    private Double purchasePricePerGram;
    private Double currentPricePerGram;
    private String storageType;    // Bank Locker, Home, Digital
    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
