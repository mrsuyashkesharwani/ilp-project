package com.example.first.Dto;

import lombok.Data;

@Data
public class BuyStockRequestDto {
    private Long userId;
    private Long stockId;
    private Integer quantity;
}
