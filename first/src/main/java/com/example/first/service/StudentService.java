package com.example.first.service;

import com.example.first.Dto.*;
import com.example.first.entity.*;

import com.example.first.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.example.first.entity.Expense;
import com.example.first.entity.Investment;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Service
public class StudentService {

    public final StudentRepo studentRepo;
    public final UserRepo userRepo;
    public final ExpenseRepo expanseRepo;
    public  final InvestmentRepo investmentRepo;
    public final StockRepo stockRepo;
    public final GoldRepo goldRepo;
    public final GoalRepo goalRepo;

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

    @Transactional
    public UserDto createdNewUser(UserDto addStudent) {
        User newUser = new User();
        newUser.setName(addStudent.getName());
        newUser.setPassword(addStudent.getPassword());
        newUser.setEmail(addStudent.getEmail());
        User user = userRepo.save(newUser);
        return new UserDto(user.getName(), user.getEmail(), null);
    }

    @Transactional
    public LoginResponseDto loginUser(LoginDto dto) {

        User user = userRepo.findByEmail(dto.getEmail());

        if (user == null) {

            return new LoginResponseDto(
                    false,
                    null,
                    null,
                    "Email not found"
            );
        }

        if (user.getPassword() == null || !user.getPassword().equals(dto.getPassword())) {

            return new LoginResponseDto(
                    false,
                    user.getUserId(),
                    user.getName(),
                    "Wrong password"
            );
        }
       return new LoginResponseDto(
               true,
               user.getUserId(),
               user.getName(),
               "Login Successful"
       );

    }

    @Transactional
    public ExpanseDto createExpanse( ExpanseDto ex) throws Exception {
        User us = userRepo.findById(ex.getUserId())
                .orElseThrow(() -> new Exception("User not found"));

        Expense expense = new Expense();

        expense.setTitle(ex.getTitle());
        expense.setAmount(ex.getAmount());
        expense.setCategory(ex.getCategory());
        expense.setExpenseDate(ex.getExpenseDate());

        expense.setUsertemp(us);

        Expense savedExpense =expanseRepo.save(expense);

        return new ExpanseDto(savedExpense.getExpenseId(),
                savedExpense.getTitle(),
                savedExpense.getAmount(),
                savedExpense.getCategory(),
                savedExpense.getExpenseDate(),
                savedExpense.getUsertemp().getUserId());




  }






    @Transactional
    public InvestmentDto Inverstment(InvestmentDto ex) throws Exception {
    User us = userRepo.findById(ex.getUserId())
            .orElseThrow(() -> new Exception("User not found"));
    Investment inv = new Investment();
    inv.setStockName(ex.getStockName());
    inv.setInvestedAmount(ex.getInvestedAmount());
    inv.setQuantity(ex.getQuantity());
    inv.setRiskPercent(ex.getRiskPercent());
    inv.setInvestmentDate(ex.getInvestmentDate());

    inv.setUsertemp(us);

    Investment savedInvestment = investmentRepo.save(inv);
    ExpanseDto expanseDto =new ExpanseDto();
    expanseDto.setTitle("Buy Stock " + ex.getStockName());
    expanseDto.setAmount(ex.getInvestedAmount());
    expanseDto.setCategory("Stock_Credited");
    expanseDto.setUserId(ex.getUserId());
    expanseDto.setExpenseDate(ex.getInvestmentDate());

    createExpanse(expanseDto);

    return new InvestmentDto(
            savedInvestment.getInvestmentId(),
            savedInvestment.getStockName(),
            savedInvestment.getInvestedAmount(),
            savedInvestment.getQuantity(),
            savedInvestment.getRiskPercent(),
            savedInvestment.getInvestmentDate(),
            savedInvestment.getUsertemp().getUserId()
    );



}
    public void StockAdd( ExpanseDto ex) throws Exception {


        Stock st  = new Stock();
       st.setCompanyName("ABC");
       st.setStockPrice((double)2000);
       st.setRiskPercent(12.34);
        stockRepo.save(st);






    }

    @Transactional
    public List<ExpanseDto> getExpensesByUser(Long userId) throws Exception {
        User us = userRepo.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        return us.getExpenses().stream().map(e -> new ExpanseDto(
                e.getExpenseId(),
                e.getTitle(),
                e.getAmount(),
                e.getCategory(),
                e.getExpenseDate(),
                userId
        )).toList();
    }

    @Transactional
    public List<InvestmentDto> getInvestmentsByUser(Long userId) throws Exception {
        User us = userRepo.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        return us.getInvestments().stream().map(inv -> new InvestmentDto(
                inv.getInvestmentId(),
                inv.getStockName(),
                inv.getInvestedAmount(),
                inv.getQuantity(),
                inv.getRiskPercent(),
                inv.getInvestmentDate(),
                userId
        )).toList();
    }

    public List<ExpanseDto> getAllStocks() {
        return stockRepo.findAll().stream().map(s -> {
            ExpanseDto dto = new ExpanseDto();
            dto.setTitle(s.getCompanyName());
            dto.setAmount(s.getStockPrice());
            dto.setCategory(String.valueOf(s.getRiskPercent()));
            return dto;
        }).toList();
    }

    @Transactional
    public String deleteExpense(Long expenseId) throws Exception {
        Expense expense = expanseRepo.findById(expenseId)
                .orElseThrow(() -> new Exception("Expense not found: " + expenseId));
        User user = expense.getUsertemp();
        if (user != null) {
            user.getExpenses().remove(expense);
            expense.setUsertemp(null);
        }
        expanseRepo.delete(expense);
        return "Expense deleted successfully";
    }

    @Transactional
    public String deleteInvestment(Long investmentId) throws Exception {
        Investment investment = investmentRepo.findById(investmentId)
                .orElseThrow(() -> new Exception("Investment not found: " + investmentId));
        User user = investment.getUsertemp();
        if (user != null) {
            user.getInvestments().remove(investment);
            investment.setUsertemp(null);
        }
        investmentRepo.delete(investment);
        return "Investment deleted successfully";
    }

    @Transactional
    public GoldDto addGold(GoldDto dto) throws Exception {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new Exception("User not found"));
        Gold gold = new Gold();
        gold.setType(dto.getType());
        gold.setQuantityGrams(dto.getQuantityGrams());
        gold.setPurchasePricePerGram(dto.getPurchasePricePerGram());
        gold.setCurrentPricePerGram(dto.getCurrentPricePerGram());
        gold.setStorageType(dto.getStorageType());
        gold.setNotes(dto.getNotes());
        gold.setPurchaseDate(dto.getPurchaseDate());
        gold.setUser(user);
        Gold saved = goldRepo.save(gold);
        return mapGoldToDto(saved);
    }

    @Transactional
    public List<GoldDto> getGoldByUser(Long userId) throws Exception {
        userRepo.findById(userId).orElseThrow(() -> new Exception("User not found"));
        return goldRepo.findByUserUserId(userId).stream().map(this::mapGoldToDto).toList();
    }

    @Transactional
    public void deleteGold(Long goldId) throws Exception {
        if (!goldRepo.existsById(goldId)) {
            throw new Exception("Gold entry not found");
        }
        goldRepo.deleteById(goldId);
    }

    private GoldDto mapGoldToDto(Gold g) {
        GoldDto dto = new GoldDto();
        dto.setGoldId(g.getGoldId());
        dto.setType(g.getType());
        dto.setQuantityGrams(g.getQuantityGrams());
        dto.setPurchasePricePerGram(g.getPurchasePricePerGram());
        dto.setCurrentPricePerGram(g.getCurrentPricePerGram());
        dto.setStorageType(g.getStorageType());
        dto.setNotes(g.getNotes());
        dto.setPurchaseDate(g.getPurchaseDate());
        dto.setUserId(g.getUser().getUserId());

        double totalInvestment = g.getQuantityGrams() * g.getPurchasePricePerGram();
        double currentValue = g.getQuantityGrams() * g.getCurrentPricePerGram();
        double profitLoss = currentValue - totalInvestment;
        dto.setTotalInvestment(totalInvestment);
        dto.setCurrentValue(currentValue);
        dto.setProfitLoss(profitLoss);
        dto.setProfitLossPct(totalInvestment == 0 ? 0 : (profitLoss / totalInvestment) * 100);
        return dto;
    }

    @Transactional
    public Map<String, Object> getWellnessScore(Long userId) throws Exception {
        User user = userRepo.findById(userId).orElseThrow(() -> new Exception("User not found"));

        double totalIncome = user.getExpenses().stream()
                .filter(e -> List.of("Salary","Freelance","Business","Rental","Other").contains(e.getCategory()))
                .mapToDouble(Expense::getAmount).sum();

        double totalExpenses = user.getExpenses().stream()
                .filter(e -> !List.of("Salary","Freelance","Business","Rental","Other").contains(e.getCategory()))
                .mapToDouble(Expense::getAmount).sum();

        double totalInvested = user.getInvestments().stream()
                .mapToDouble(Investment::getInvestedAmount).sum();

        double savingsRate = totalIncome == 0 ? 0 : ((totalIncome - totalExpenses) / totalIncome) * 100;
        double investmentRate = totalIncome == 0 ? 0 : (totalInvested / totalIncome) * 100;

        int score = 50;
        if (savingsRate >= 20) score += 20;
        else if (savingsRate >= 10) score += 10;
        if (investmentRate >= 15) score += 20;
        else if (investmentRate >= 5) score += 10;
        if (totalIncome > 0) score += 10;
        score = Math.min(score, 100);

        String level = score >= 80 ? "Excellent" : score >= 60 ? "Good" : score >= 40 ? "Fair" : "Needs Improvement";

        return Map.of(
                "score", score,
                "level", level,
                "savingsRate", Math.round(savingsRate * 10.0) / 10.0,
                "investmentRate", Math.round(investmentRate * 10.0) / 10.0,
                "totalIncome", totalIncome,
                "totalExpenses", totalExpenses,
                "totalInvested", totalInvested
        );
    }

    @Transactional
    public GoalDto createGoal(GoalDto dto) throws Exception {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new Exception("User not found"));
        Goal goal = new Goal();
        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setSavedAmount(dto.getSavedAmount() != null ? dto.getSavedAmount() : 0.0);
        goal.setTargetDate(dto.getTargetDate());
        goal.setStatus(dto.getStatus() != null ? dto.getStatus() : "Not Started");
        goal.setIcon(dto.getIcon());
        goal.setUser(user);
        Goal saved = goalRepo.save(goal);
        return mapGoal(saved, dto.getUserId());
    }

    @Transactional
    public List<GoalDto> getGoalsByUser(Long userId) throws Exception {
        userRepo.findById(userId).orElseThrow(() -> new Exception("User not found"));
        return goalRepo.findByUserUserId(userId).stream()
                .map(g -> mapGoal(g, userId))
                .toList();
    }

    @Transactional
    public GoalDto updateGoalProgress(Long goalId, Double additionalAmount) throws Exception {
        Goal goal = goalRepo.findById(goalId)
                .orElseThrow(() -> new Exception("Goal not found"));
        double newSaved = (goal.getSavedAmount() != null ? goal.getSavedAmount() : 0) + additionalAmount;
        newSaved = Math.min(newSaved, goal.getTargetAmount());
        goal.setSavedAmount(newSaved);
        double pct = goal.getTargetAmount() > 0 ? (newSaved / goal.getTargetAmount()) * 100 : 0;
        if (pct <= 0)        goal.setStatus("Not Started");
        else if (pct >= 100) goal.setStatus("Completed");
        else                 goal.setStatus("In Progress");
        Goal saved = goalRepo.save(goal);
        return mapGoal(saved, goal.getUser().getUserId());
    }

    @Transactional
    public void deleteGoal(Long goalId) throws Exception {
        Goal goal = goalRepo.findById(goalId)
                .orElseThrow(() -> new Exception("Goal not found: " + goalId));
        User user = goal.getUser();
        if (user != null) {
            user.getGoals().remove(goal);
            goal.setUser(null);
        }
        goalRepo.delete(goal);
    }

    private GoalDto mapGoal(Goal g, Long userId) {
        return new GoalDto(
            g.getGoalId(),
            g.getName(),
            g.getTargetAmount(),
            g.getSavedAmount(),
            g.getTargetDate(),
            g.getStatus(),
            g.getIcon(),
            userId
        );
    }

    @Transactional
    public InvestmentDto buyMoreStock(BuyMoreDto dto) throws Exception {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new Exception("User not found"));

        Investment existing = user.getInvestments().stream()
                .filter(i -> i.getStockName().equalsIgnoreCase(dto.getStockName()))
                .findFirst()
                .orElse(null);

        double additionalAmount = dto.getAdditionalQuantity() * dto.getPricePerUnit();

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + dto.getAdditionalQuantity());
            existing.setInvestedAmount(existing.getInvestedAmount() + additionalAmount);
            Investment saved = investmentRepo.save(existing);

            Expense record = new Expense();
            record.setTitle("Buy Stock " + dto.getStockName() +
                            " (" + dto.getAdditionalQuantity() + " units)");
            record.setAmount(additionalAmount);
            record.setCategory("Stock_Buy");
            record.setExpenseDate(dto.getPurchaseDate());
            record.setUsertemp(user);
            expanseRepo.save(record);

            return new InvestmentDto(
                    saved.getInvestmentId(), saved.getStockName(),
                    saved.getInvestedAmount(), saved.getQuantity(),
                    saved.getRiskPercent(), saved.getInvestmentDate(), dto.getUserId());
        } else {
            Investment newInv = new Investment();
            newInv.setStockName(dto.getStockName());
            newInv.setQuantity(dto.getAdditionalQuantity());
            newInv.setInvestedAmount(additionalAmount);
            newInv.setRiskPercent(0.0);
            newInv.setInvestmentDate(dto.getPurchaseDate());
            newInv.setUsertemp(user);
            Investment saved = investmentRepo.save(newInv);

            Expense record = new Expense();
            record.setTitle("Buy Stock " + dto.getStockName() +
                            " (" + dto.getAdditionalQuantity() + " units)");
            record.setAmount(additionalAmount);
            record.setCategory("Stock_Buy");
            record.setExpenseDate(dto.getPurchaseDate());
            record.setUsertemp(user);
            expanseRepo.save(record);

            return new InvestmentDto(
                    saved.getInvestmentId(), saved.getStockName(),
                    saved.getInvestedAmount(), saved.getQuantity(),
                    saved.getRiskPercent(), saved.getInvestmentDate(), dto.getUserId());
        }
    }

    @Transactional
    public InvestmentDto sellStock(SellStockDto dto) throws Exception {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new Exception("User not found"));

        Investment investment = investmentRepo.findById(dto.getInvestmentId())
                .orElseThrow(() -> new Exception("Investment not found"));

        if (dto.getSellQuantity() > investment.getQuantity()) {
            throw new Exception("Cannot sell more than owned quantity: " + investment.getQuantity());
        }

        double saleAmount  = dto.getSellQuantity() * dto.getSellPricePerUnit();
        double costPerUnit = investment.getInvestedAmount() / investment.getQuantity();
        int    remainingQty = investment.getQuantity() - dto.getSellQuantity();

        Expense saleRecord = new Expense();
        saleRecord.setTitle("Sell Stock " + investment.getStockName() +
                            " (" + dto.getSellQuantity() + " units @ ₹" +
                            dto.getSellPricePerUnit() + ")");
        saleRecord.setAmount(saleAmount);
        saleRecord.setCategory("Stock_Sale");
        saleRecord.setExpenseDate(java.time.LocalDate.now());
        saleRecord.setUsertemp(user);
        expanseRepo.save(saleRecord);

        if (remainingQty <= 0) {
            user.getInvestments().remove(investment);
            investment.setUsertemp(null);
            investmentRepo.delete(investment);

            return new InvestmentDto(
                    null, investment.getStockName(),
                    0.0, 0,
                    investment.getRiskPercent(),
                    investment.getInvestmentDate(), dto.getUserId());
        } else {
            investment.setQuantity(remainingQty);
            investment.setInvestedAmount(costPerUnit * remainingQty);
            Investment saved = investmentRepo.save(investment);

            return new InvestmentDto(
                    saved.getInvestmentId(), saved.getStockName(),
                    saved.getInvestedAmount(), saved.getQuantity(),
                    saved.getRiskPercent(), saved.getInvestmentDate(), dto.getUserId());
        }
    }

}
