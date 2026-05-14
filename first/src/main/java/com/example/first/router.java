package com.example.first;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class router {
  @GetMapping("/book")
 public  String fun()
  {
    return "Hare krishna";

  }
}
