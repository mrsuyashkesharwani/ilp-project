package com.example.first.controller;


import com.example.first.Dto.*;
import com.example.first.entity.Expense;
import com.example.first.service.StudentService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FinicialController {

    private final StudentService studentService;

    @PostMapping("/user")
    public UserDto createStudent(@RequestBody UserDto addStudent )
    {
        return studentService.createdNewUser(addStudent);

    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginDto dto) {

        return studentService.loginUser(dto);
    }


    @PostMapping("/Expanse")
    public ExpanseDto createStudent(@RequestBody ExpanseDto ex) throws Exception {
        return studentService.createExpanse(ex);


    }

    @PostMapping("/investment")
    public InvestmentDto investment (@RequestBody InvestmentDto ex) throws Exception {
        return studentService.Inverstment(ex);


    }

    @PostMapping("/Stock")
    public void Stock (@RequestBody ExpanseDto ex) throws Exception {
        studentService.StockAdd(ex);


    } 
    
    @PostMapping("/buyStock")
    public void BuyStock (@RequestBody ExpanseDto ex) throws Exception {



    }
    @PatchMapping("/sellStock")
    public void sellStock (@RequestBody ExpanseDto ex) throws Exception {



    }











}
