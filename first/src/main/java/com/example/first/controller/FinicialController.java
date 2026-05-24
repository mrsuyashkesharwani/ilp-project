package com.example.first.controller;

import com.example.first.Dto.*;
import com.example.first.entity.Expense;
import com.example.first.repo.StockRepo;
import com.example.first.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class FinicialController {

    private final StudentService studentService;
    private final StockRepo stockRepo;

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody UserDto dto) {
        try { return ResponseEntity.ok(studentService.createdNewUser(dto)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        try { return ResponseEntity.ok(studentService.loginUser(dto)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @PostMapping("/Expanse")
    public ResponseEntity<?> createExpanse(@RequestBody ExpanseDto ex) {
        try { return ResponseEntity.ok(studentService.createExpanse(ex)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/Expanse/{userId}")
    public ResponseEntity<?> getExpenses(@PathVariable Long userId) {
        try { return ResponseEntity.ok(studentService.getExpensesByUser(userId)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @DeleteMapping("/Expanse/{id}")
    public ResponseEntity<String> deleteExpanse(@PathVariable Long id) {
        try { return ResponseEntity.ok(studentService.deleteExpense(id)); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }

    @PostMapping("/investment")
    public ResponseEntity<?> investment(@RequestBody InvestmentDto ex) {
        try { return ResponseEntity.ok(studentService.Inverstment(ex)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/investment/{userId}")
    public ResponseEntity<?> getInvestments(@PathVariable Long userId) {
        try { return ResponseEntity.ok(studentService.getInvestmentsByUser(userId)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @DeleteMapping("/investment/{id}")
    public ResponseEntity<String> deleteInvestment(@PathVariable Long id) {
        try { return ResponseEntity.ok(studentService.deleteInvestment(id)); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }

    @PostMapping("/investment/buy-more")
    public ResponseEntity<?> buyMoreStock(@RequestBody BuyMoreDto dto) {
        try { return ResponseEntity.ok(studentService.buyMoreStock(dto)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @PostMapping("/investment/sell")
    public ResponseEntity<?> sellStock(@RequestBody SellStockDto dto) {
        try { return ResponseEntity.ok(studentService.sellStock(dto)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @PostMapping("/gold")
    public ResponseEntity<?> addGold(@RequestBody GoldDto dto) {
        try { return ResponseEntity.ok(studentService.addGold(dto)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/gold/{userId}")
    public ResponseEntity<?> getGold(@PathVariable Long userId) {
        try { return ResponseEntity.ok(studentService.getGoldByUser(userId)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @DeleteMapping("/gold/{goldId}")
    public ResponseEntity<String> deleteGold(@PathVariable Long goldId) {
        try { studentService.deleteGold(goldId); return ResponseEntity.ok("Gold entry deleted"); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }

    @PostMapping("/goal")
    public ResponseEntity<?> createGoal(@RequestBody GoalDto dto) {
        try { return ResponseEntity.ok(studentService.createGoal(dto)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/goal/{userId}")
    public ResponseEntity<?> getGoals(@PathVariable Long userId) {
        try { return ResponseEntity.ok(studentService.getGoalsByUser(userId)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @PatchMapping("/goal/{goalId}/progress")
    public ResponseEntity<?> updateGoalProgress(
            @PathVariable Long goalId, @RequestParam Double amount) {
        try { return ResponseEntity.ok(studentService.updateGoalProgress(goalId, amount)); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }

    @DeleteMapping("/goal/{goalId}")
    public ResponseEntity<String> deleteGoal(@PathVariable Long goalId) {
        try { studentService.deleteGoal(goalId); return ResponseEntity.ok("Goal deleted successfully"); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }

    @GetMapping("/wellness/{userId}")
    public ResponseEntity<?> getWellness(@PathVariable Long userId) {
        try { return ResponseEntity.ok(studentService.getWellnessScore(userId)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/stocks")
    public ResponseEntity<?> getAllStocks() {
        try { return ResponseEntity.ok(stockRepo.findAll()); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/recommend")
    public ResponseEntity<?> getRecommendations(
            @RequestParam Double amount,
            @RequestParam String risk) {
        try { return ResponseEntity.ok(studentService.getRecommendations(amount, risk)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/gold/prices")
    public ResponseEntity<?> getGoldMarketPrices() {
        try { return ResponseEntity.ok(studentService.getGoldMarketPrices()); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/risk/{userId}")
    public ResponseEntity<?> getRiskProfile(@PathVariable Long userId) {
        try { return ResponseEntity.ok(studentService.getRiskProfile(userId)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }

    @GetMapping("/wallet/{userId}")
    public ResponseEntity<?> getWalletBalance(@PathVariable Long userId) {
        try { return ResponseEntity.ok(studentService.getWalletBalance(userId)); }
        catch (Exception e) { return ResponseEntity.status(500).body(e.getMessage()); }
    }
}
