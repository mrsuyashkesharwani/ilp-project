package com.example.first.repo;

import  com.example.first.entity.studentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepo extends JpaRepository<studentEntity,Long> {


}
