package com.example.first.Dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpanseDto {



    private Long expenseId;

    private String title;

    private Double amount;

    private String category;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;

    // User id for relation
    private Long userId;

}
