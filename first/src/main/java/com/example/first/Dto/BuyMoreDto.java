package com.example.first.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyMoreDto {
    private Long   userId;
    private String stockName;
    private Integer additionalQuantity;
    private Double  pricePerUnit;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;
}
