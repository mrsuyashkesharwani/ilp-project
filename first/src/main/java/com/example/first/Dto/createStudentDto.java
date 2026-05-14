package com.example.first.Dto;

import jakarta.persistence.Entity;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class createStudentDto {

   private  String name;
   private  String email;



}
