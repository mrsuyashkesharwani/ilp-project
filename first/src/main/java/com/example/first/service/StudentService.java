package com.example.first.service;

import com.example.first.Dto.ExpanseDto;
import com.example.first.Dto.UserDto;
import com.example.first.Dto.studentDto;
import com.example.first.Dto.createStudentDto;
import com.example.first.entity.*;

import com.example.first.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class StudentService {

    public final StudentRepo studentRepo;
    public final UserRepo userRepo;
    public final ExpenseRepo expanseRepo;
    public  final InvestmentRepo investmentRepo;
    public final StockRepo stockRepo;

    public List<studentDto> getAllStudent() {
        List<studentEntity> list = studentRepo.findAll();
        return list.stream().map(element -> new studentDto(element.getId(), element.getName(), element.getEmail())).toList();
    }

    public studentDto findById(Long Id) {
        studentEntity student = studentRepo.findById(Id).orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return new studentDto(student.getId(), student.getName(), student.getEmail());
    }

    public studentDto createdNewStudent(createStudentDto addStudent) {
        studentEntity newStudent = new studentEntity();
        newStudent.setName(addStudent.getName());
        newStudent.setName(addStudent.getEmail());
        studentEntity student = studentRepo.save(newStudent);
        return new studentDto(student.getId(), student.getName(), student.getEmail());
    }


    public studentDto updatedStudent(Long id, Map<String, Object> updates) {
        studentEntity student = studentRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Student not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    student.setName((String) value);
                    break;
                case "email":
                    student.setEmail((String) value);
                    break;
                default:
                    throw new IllegalArgumentException("Field are not exist");
            }

        });

        studentEntity saveStudent = studentRepo.save(student);
        return new studentDto(saveStudent.getId(), saveStudent.getName(), saveStudent.getEmail());

    }

    public UserDto createdNewUser(UserDto addStudent) {
        User newUser = new User();
        newUser.setName(addStudent.getName());
        newUser.setPassword(addStudent.getPassword());
        newUser.setEmail(addStudent.getEmail());
        User user = userRepo.save(newUser);
        return new UserDto(user.getName(), user.getEmail(), user.getPassword());
    }


    public void createExpanse( ExpanseDto ex) throws Exception {
        User us = userRepo.findById(ex.getUserid())
                .orElseThrow(() -> new Exception("User not found"));

        Expense expense = new Expense();

        expense.setAmount(ex.getAmount());
        expense.setTitle(ex.getTitle());

        expense.setUsertemp(us);

        expanseRepo.save(expense);

        List<Expense> ans= new ArrayList<>();
        ans = us.getExpenses();

        for (Expense an : ans) {
            System.out.println(an.getTitle());


        }




  }






public void Inverstment( ExpanseDto ex) throws Exception {
    User us = userRepo.findById(ex.getUserid())
            .orElseThrow(() -> new Exception("User not found"));

    Investment inv  = new Investment();
   inv.setInvestedAmount((double)2000);
   inv.setStockName("book");

    inv.setUsertemp(us);

      investmentRepo.save(inv);




}
    public void StockAdd( ExpanseDto ex) throws Exception {


        Stock st  = new Stock();
       st.setCompanyName("ABC");
       st.setStockPrice((double)2000);
       st.setRiskPercent(12.34);
        stockRepo.save(st);






    }




}
