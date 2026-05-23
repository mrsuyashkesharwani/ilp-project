package com.example.first.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private boolean status;
    private Long userId;
    private String name;
    private String message;
    private String token;
    private String role;
}
