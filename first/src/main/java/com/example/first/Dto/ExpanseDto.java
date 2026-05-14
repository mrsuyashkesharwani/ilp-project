package com.example.first.Dto;


import com.example.first.entity.User;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpanseDto {



    private String title;
    private double amount;
     private Long userid;

}
