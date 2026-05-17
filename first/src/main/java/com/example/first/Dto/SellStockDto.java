package com.example.first.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellStockDto {
    private Long    userId;
    private Long    investmentId;
    private Integer sellQuantity;
    private Double  sellPricePerUnit;
}
