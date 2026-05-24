package com.example.first.Dto;

import lombok.Data;

@Data
public class SellStockRequestDto {
    private Long userId;
    private Long buyStockId;    // the BuyStock record id
    private Integer quantity;   // how many units to sell
}
