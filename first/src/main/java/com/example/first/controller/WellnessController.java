package com.example.first.controller;

import com.example.first.service.FinancialWellnessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wellness")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class WellnessController {

    private final FinancialWellnessService wellnessService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getWellness(@PathVariable Long userId) {
        try { return ResponseEntity.ok(wellnessService.computeWellness(userId)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }
}
