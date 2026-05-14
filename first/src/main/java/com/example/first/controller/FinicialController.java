package com.example.first.controller;


import com.example.first.Dto.ExpanseDto;
import com.example.first.Dto.UserDto;
import com.example.first.Dto.createStudentDto;
import com.example.first.Dto.studentDto;
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

    @PostMapping("/Expanse")
    public void createStudent(@RequestBody ExpanseDto ex) throws Exception {
        studentService.createExpanse(ex);


    }

    @PostMapping("/investment")
    public void investment (@RequestBody ExpanseDto ex) throws Exception {
        studentService.Inverstment(ex);


    }

    @PostMapping("/Stock")
    public void Stock (@RequestBody ExpanseDto ex) throws Exception {
        studentService.StockAdd(ex);


    }






}
