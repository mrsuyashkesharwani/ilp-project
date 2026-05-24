package com.example.first.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    private boolean status;

    private Long userId;

    private String name;

    private String message;

    private String token;

    private String role;
}
