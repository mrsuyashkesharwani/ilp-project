package com.example.first.controller;

import com.example.first.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getRecommendations(@PathVariable Long userId) {
        try { return ResponseEntity.ok(recommendationService.getRecommendations(userId)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }
}
