package com.example.first.service;

import com.example.first.Dto.*;
import com.example.first.config.JwtUtil;
import com.example.first.entity.*;
import com.example.first.repo.*;
import com.example.first.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class StudentService {

    public final StudentRepo studentRepo;
    public final UserRepo userRepo;
    public final ExpenseRepo expanseRepo;
    public final InvestmentRepo investmentRepo;
    public final StockRepo stockRepo;
    public final GoldRepo goldRepo;
    public final GoalRepo goalRepo;
    public final IncomeLogRepo incomeLogRepo;
    public final SystemConfigRepo configRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Student CRUD kept for backward compatibility if needed
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
                    throw new IllegalArgumentException("Field does not exist");
            }
        });
        studentEntity saveStudent = studentRepo.save(student);
        return new studentDto(saveStudent.getId(), saveStudent.getName(), saveStudent.getEmail());
    }

    // AUTHENTICATION AND REGISTRATION
    @Transactional
    public UserDto createdNewUser(UserDto dto) throws Exception {
        if (userRepo.findByEmail(dto.getEmail()) != null) {
            throw new Exception("Email is already associated with an account. Try logging in instead.");
        }
        User newUser = new User();
        newUser.setName(dto.getName());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setMobileNo(dto.getMobileNo());
        newUser.setRole("ROLE_USER");
        newUser.setWalletBalance(0.0);
        newUser.setStatus("ACTIVE");
        User user = userRepo.save(newUser);
        return new UserDto(user.getName(), user.getEmail(), user.getMobileNo(), null);
    }

    @Transactional
    public LoginResponseDto loginUser(LoginDto dto) {
        User user = userRepo.findByEmail(dto.getEmail());
        if (user == null) {
            return new LoginResponseDto(false, null, null, "Email not found", null, null);
        }
        if ("BLOCKED".equalsIgnoreCase(user.getStatus())) {
            return new LoginResponseDto(false, null, null, "Account is blocked. Contact support.", null, null);
        }
        if (user.getPassword() == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return new LoginResponseDto(false, user.getUserId(), user.getName(), "Wrong password", null, null);
        }

        String token = jwtUtil.generateToken(user);
        String roleName = user.getRole().replace("ROLE_", "").toLowerCase(); // returns "user" or "admin"

        return new LoginResponseDto(
                true,
                user.getUserId(),
                user.getName(),
                "Login Successful",
                token,
                roleName
        );
    }

    // EXPENSES AND INCOME LOGS
    @Transactional
    public ExpanseDto createExpanse(ExpanseDto ex) throws Exception {
        User us = userRepo.findById(ex.getUserId())
                .orElseThrow(() -> new Exception("User not found"));

        if ("BLOCKED".equalsIgnoreCase(us.getStatus())) {
            throw new Exception("Account is blocked.");
        }

        List<String> incomeCategories = List.of("Salary", "Freelance", "Business", "Rental", "Other", "Stock_Sale");

        if (incomeCategories.contains(ex.getCategory())) {
            // Save to IncomeLog
            IncomeLog income = new IncomeLog();
            income.setAmount(ex.getAmount());
            income.setSource(ex.getCategory());
            income.setIncomeDate(ex.getExpenseDate());
            income.setUser(us);
            if ("Stock_Sale".equals(ex.getCategory())) {
                income.setProfit(ex.getProfit() != null ? ex.getProfit() : ex.getAmount());
            } else {
                income.setProfit(ex.getAmount());
            }
            IncomeLog savedIncome = incomeLogRepo.save(income);

            // Update Wallet Balance
            us.setWalletBalance(us.getWalletBalance() + ex.getAmount());
            userRepo.save(us);

            // Recalculate Goal Progress automatically after new income
            recalculateGoalProgress(us);

            ExpanseDto result = new ExpanseDto(
                    savedIncome.getIncomeId(),
                    ex.getTitle(),
                    savedIncome.getAmount(),
                    savedIncome.getSource(),
                    savedIncome.getIncomeDate(),
                    us.getUserId()
            );
            result.setProfit(savedIncome.getProfit());
            return result;
        } else {
            // Check Wallet Balance for expenses
            if (us.getWalletBalance() < ex.getAmount()) {
                throw new Exception("Transaction failed: Your current balance is ₹" + String.format("%,.0f", us.getWalletBalance()) +
                        ". You cannot add an expense of ₹" + String.format("%,.0f", ex.getAmount()) + ". Please log income to top up your wallet.");
            }

            Expense expense = new Expense();
            expense.setTitle(ex.getTitle());
            expense.setAmount(ex.getAmount());
            expense.setCategory(ex.getCategory());
            expense.setExpenseDate(ex.getExpenseDate());
            expense.setUsertemp(us);
            Expense savedExpense = expanseRepo.save(expense);

            // Deduct Wallet Balance
            us.setWalletBalance(us.getWalletBalance() - ex.getAmount());
            userRepo.save(us);

            // Recalculate Goal Progress automatically after new expense
            recalculateGoalProgress(us);

            return new ExpanseDto(
                    savedExpense.getExpenseId(),
                    savedExpense.getTitle(),
                    savedExpense.getAmount(),
                    savedExpense.getCategory(),
                    savedExpense.getExpenseDate(),
                    us.getUserId()
            );
        }
    }

    @Transactional
    public List<ExpanseDto> getExpensesByUser(Long userId) throws Exception {
        User us = userRepo.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        List<ExpanseDto> list = new ArrayList<>();

        // Add actual Expenses
        us.getExpenses().forEach(e -> list.add(new ExpanseDto(
                e.getExpenseId(),
                e.getTitle(),
                e.getAmount(),
                e.getCategory(),
                e.getExpenseDate(),
                userId
        )));

        // Add Income Logs
        incomeLogRepo.findByUserUserId(userId).forEach(i -> {
            ExpanseDto dto = new ExpanseDto(
                i.getIncomeId(),
                i.getSource() + " Logged",
                i.getAmount(),
                i.getSource(),
                i.getIncomeDate(),
                userId
            );
            dto.setProfit(i.getProfit() != null ? i.getProfit() : i.getAmount());
            list.add(dto);
        });

        return list;
    }

    @Transactional
    public String deleteExpense(Long id) throws Exception {
        if (expanseRepo.existsById(id)) {
            Expense expense = expanseRepo.findById(id).get();
            User user = expense.getUsertemp();
            if (user != null) {
                user.setWalletBalance(user.getWalletBalance() + expense.getAmount());
                user.getExpenses().remove(expense);
                expense.setUsertemp(null);
                userRepo.save(user);
                recalculateGoalProgress(user);
            }
            expanseRepo.delete(expense);
            return "Expense deleted successfully";
        } else if (incomeLogRepo.existsById(id)) {
            IncomeLog income = incomeLogRepo.findById(id).get();
            User user = income.getUser();
            if (user != null) {
                if (user.getWalletBalance() < income.getAmount()) {
                    throw new Exception("Cannot delete income log: leaves wallet balance negative (₹" +
                            String.format("%,.0f", user.getWalletBalance() - income.getAmount()) + ").");
                }
                user.setWalletBalance(user.getWalletBalance() - income.getAmount());
                income.setUser(null);
                userRepo.save(user);
                recalculateGoalProgress(user);
            }
            incomeLogRepo.delete(income);
            return "Income log deleted successfully";
        }
        throw new Exception("Record not found: " + id);
    }

    // STOCKS INVESTMENTS
    @Transactional
    public InvestmentDto Inverstment(InvestmentDto ex) throws Exception {
        User us = userRepo.findById(ex.getUserId())
                .orElseThrow(() -> new Exception("User not found"));

        if ("BLOCKED".equalsIgnoreCase(us.getStatus())) {
            throw new Exception("Account is blocked.");
        }

        double totalCost = ex.getInvestedAmount();
        if (us.getWalletBalance() < totalCost) {
            throw new Exception("Transaction failed: Your current balance is ₹" + String.format("%,.0f", us.getWalletBalance()) +
                    ". You cannot purchase " + ex.getQuantity() + " shares of " + ex.getStockName() + " for ₹" + String.format("%,.0f", totalCost) +
                    ". Please log income to top up your wallet.");
        }

        Investment inv = new Investment();
        inv.setStockName(ex.getStockName());
        inv.setInvestedAmount(ex.getInvestedAmount());
        inv.setQuantity(ex.getQuantity());
        inv.setRiskPercent(ex.getRiskPercent());
        inv.setInvestmentDate(ex.getInvestmentDate());
        inv.setUsertemp(us);
        Investment savedInvestment = investmentRepo.save(inv);

        // Auto-create expense record via createExpanse to deduct wallet balance
        ExpanseDto expanseDto = new ExpanseDto();
        expanseDto.setTitle("Buy Stock " + ex.getStockName());
        expanseDto.setAmount(ex.getInvestedAmount());
        expanseDto.setCategory("Stock_Buy");
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
                us.getUserId()
        );
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
            dto.setTitle(s.getCompanyName() + " (" + s.getSymbol() + ")");
            dto.setAmount(s.getCurrentPrice());
            dto.setCategory(s.getRiskCategory()); // LOW / MEDIUM / HIGH
            return dto;
        }).toList();
    }

    @Transactional
    public String deleteInvestment(Long investmentId) throws Exception {
        Investment investment = investmentRepo.findById(investmentId)
                .orElseThrow(() -> new Exception("Investment not found: " + investmentId));
        User user = investment.getUsertemp();
        if (user != null) {
            // Refund investment cost on simple deletion
            user.setWalletBalance(user.getWalletBalance() + investment.getInvestedAmount());
            user.getInvestments().remove(investment);
            investment.setUsertemp(null);
            userRepo.save(user);
            recalculateGoalProgress(user);
        }
        investmentRepo.delete(investment);
        return "Investment deleted successfully";
    }

    @Transactional
    public InvestmentDto buyMoreStock(BuyMoreDto dto) throws Exception {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new Exception("User not found"));

        if ("BLOCKED".equalsIgnoreCase(user.getStatus())) {
            throw new Exception("Account is blocked.");
        }

        double additionalAmount = dto.getAdditionalQuantity() * dto.getPricePerUnit();
        if (user.getWalletBalance() < additionalAmount) {
            throw new Exception("Transaction failed: Your current balance is ₹" + String.format("%,.0f", user.getWalletBalance()) +
                    ". You cannot purchase " + dto.getAdditionalQuantity() + " shares of " + dto.getStockName() + " for ₹" + String.format("%,.0f", additionalAmount) +
                    ". Please log income to top up your wallet.");
        }

        Investment existing = user.getInvestments().stream()
                .filter(i -> i.getStockName().equalsIgnoreCase(dto.getStockName()))
                .findFirst()
                .orElse(null);

        // Deduct from wallet & record transaction
        ExpanseDto expense = new ExpanseDto();
        expense.setTitle("Buy Stock " + dto.getStockName() + " (" + dto.getAdditionalQuantity() + " units)");
        expense.setAmount(additionalAmount);
        expense.setCategory("Stock_Buy");
        expense.setUserId(dto.getUserId());
        expense.setExpenseDate(dto.getPurchaseDate());
        createExpanse(expense);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + dto.getAdditionalQuantity());
            existing.setInvestedAmount(existing.getInvestedAmount() + additionalAmount);
            Investment saved = investmentRepo.save(existing);
            return new InvestmentDto(saved.getInvestmentId(), saved.getStockName(), saved.getInvestedAmount(),
                    saved.getQuantity(), saved.getRiskPercent(), saved.getInvestmentDate(), dto.getUserId());
        } else {
            // Find stock metadata for risk percentage
            double riskPct = stockRepo.findAll().stream()
                    .filter(s -> s.getSymbol().equalsIgnoreCase(dto.getStockName()))
                    .findFirst()
                    .map(Stock::getRiskPercent)
                    .orElse(0.0);

            Investment newInv = new Investment();
            newInv.setStockName(dto.getStockName());
            newInv.setQuantity(dto.getAdditionalQuantity());
            newInv.setInvestedAmount(additionalAmount);
            newInv.setRiskPercent(riskPct);
            newInv.setInvestmentDate(dto.getPurchaseDate());
            newInv.setUsertemp(user);
            Investment saved = investmentRepo.save(newInv);
            return new InvestmentDto(saved.getInvestmentId(), saved.getStockName(), saved.getInvestedAmount(),
                    saved.getQuantity(), saved.getRiskPercent(), saved.getInvestmentDate(), dto.getUserId());
        }
    }

    @Transactional
    public InvestmentDto sellStock(SellStockDto dto) throws Exception {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new Exception("User not found"));

        if ("BLOCKED".equalsIgnoreCase(user.getStatus())) {
            throw new Exception("Account is blocked.");
        }

        Investment investment = investmentRepo.findById(dto.getInvestmentId())
                .orElseThrow(() -> new Exception("Investment not found"));

        if (dto.getSellQuantity() > investment.getQuantity()) {
            throw new Exception("Cannot sell more than owned quantity: " + investment.getQuantity());
        }

        double saleAmount = dto.getSellQuantity() * dto.getSellPricePerUnit();
        double costPerUnit = investment.getInvestedAmount() / investment.getQuantity();
        double profit = saleAmount - (dto.getSellQuantity() * costPerUnit);
        int remainingQty = investment.getQuantity() - dto.getSellQuantity();

        // Add proceeds to wallet & register income log
        ExpanseDto saleIncome = new ExpanseDto();
        saleIncome.setTitle("Sell Stock " + investment.getStockName() + " (" + dto.getSellQuantity() + " units @ ₹" + dto.getSellPricePerUnit() + ")");
        saleIncome.setAmount(saleAmount);
        saleIncome.setCategory("Stock_Sale");
        saleIncome.setUserId(dto.getUserId());
        saleIncome.setExpenseDate(LocalDate.now());
        saleIncome.setProfit(profit);
        createExpanse(saleIncome);

        if (remainingQty <= 0) {
            user.getInvestments().remove(investment);
            investment.setUsertemp(null);
            investmentRepo.delete(investment);
            return new InvestmentDto(null, investment.getStockName(), 0.0, 0, investment.getRiskPercent(), investment.getInvestmentDate(), dto.getUserId());
        } else {
            investment.setQuantity(remainingQty);
            investment.setInvestedAmount(costPerUnit * remainingQty);
            Investment saved = investmentRepo.save(investment);
            return new InvestmentDto(saved.getInvestmentId(), saved.getStockName(), saved.getInvestedAmount(),
                    saved.getQuantity(), saved.getRiskPercent(), saved.getInvestmentDate(), dto.getUserId());
        }
    }

    // GOLD TRANSACTIONS
    @Transactional
    public GoldDto addGold(GoldDto dto) throws Exception {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new Exception("User not found"));

        if ("BLOCKED".equalsIgnoreCase(user.getStatus())) {
            throw new Exception("Account is blocked.");
        }

        double totalCost = dto.getQuantityGrams() * dto.getPurchasePricePerGram();
        if (user.getWalletBalance() < totalCost) {
            throw new Exception("Transaction failed: Your current balance is ₹" + String.format("%,.0f", user.getWalletBalance()) +
                    ". You cannot purchase " + dto.getQuantityGrams() + "g of Gold for ₹" + String.format("%,.0f", totalCost) +
                    ". Please log income to top up your wallet.");
        }

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

        // Record as expense to adjust wallet
        ExpanseDto goldExpense = new ExpanseDto();
        goldExpense.setTitle("Buy Gold (" + dto.getType() + "): " + dto.getQuantityGrams() + "g");
        goldExpense.setAmount(totalCost);
        goldExpense.setCategory("Gold");
        goldExpense.setUserId(dto.getUserId());
        goldExpense.setExpenseDate(dto.getPurchaseDate());
        createExpanse(goldExpense);

        return mapGoldToDto(saved);
    }

    @Transactional
    public List<GoldDto> getGoldByUser(Long userId) throws Exception {
        userRepo.findById(userId).orElseThrow(() -> new Exception("User not found"));

        // Update gold current price in holdings to reflect simulated gold price
        double currentGoldPrice = Double.parseDouble(configRepo.findById("gold_price")
                .map(SystemConfig::getConfigValue)
                .orElse("7200.0"));

        List<Gold> holdings = goldRepo.findByUserUserId(userId);
        for (Gold g : holdings) {
            g.setCurrentPricePerGram(currentGoldPrice);
            goldRepo.save(g);
        }

        return holdings.stream().map(this::mapGoldToDto).toList();
    }

    @Transactional
    public void deleteGold(Long goldId) throws Exception {
        Gold gold = goldRepo.findById(goldId)
                .orElseThrow(() -> new Exception("Gold entry not found"));
        User user = gold.getUser();
        if (user != null) {
            double refundAmount = gold.getQuantityGrams() * gold.getCurrentPricePerGram();
            double goldProfit = (gold.getCurrentPricePerGram() - gold.getPurchasePricePerGram()) * gold.getQuantityGrams();

            // Register proceeds as Stock_Sale / asset sale income
            ExpanseDto proceeds = new ExpanseDto();
            proceeds.setTitle("Sell Gold (" + gold.getType() + "): " + gold.getQuantityGrams() + "g");
            proceeds.setAmount(refundAmount);
            proceeds.setCategory("Stock_Sale");
            proceeds.setUserId(user.getUserId());
            proceeds.setExpenseDate(LocalDate.now());
            proceeds.setProfit(goldProfit);
            createExpanse(proceeds);
        }
        goldRepo.delete(gold);
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

    // FINANCIAL WELLNESS SCORE
    @Transactional
    public Map<String, Object> getWellnessScore(Long userId) throws Exception {
        User user = userRepo.findById(userId).orElseThrow(() -> new Exception("User not found"));

        double totalIncome = incomeLogRepo.findByUserUserId(userId).stream()
                .mapToDouble(income -> income.getProfit() != null ? income.getProfit() : income.getAmount())
                .sum();

        double totalExpenses = user.getExpenses().stream()
                .filter(e -> !List.of("Salary", "Freelance", "Business", "Rental", "Other", "Stock_Sale").contains(e.getCategory()))
                .mapToDouble(Expense::getAmount).sum();

        double totalInvested = user.getInvestments().stream()
                .mapToDouble(Investment::getInvestedAmount).sum()
                + user.getGoldHoldings().stream()
                .mapToDouble(g -> g.getQuantityGrams() * g.getPurchasePricePerGram()).sum();

        double savingsRate = totalIncome == 0 ? 0 : ((totalIncome - totalExpenses) / totalIncome) * 100;
        double investmentRate = totalIncome == 0 ? 0 : (totalInvested / totalIncome) * 100;

        // Smart weights logic
        double savingsRatioScore = Math.min((savingsRate > 0 ? savingsRate : 0) / 30.0 * 25.0, 25.0);

        double budgetAdherenceScore = 25.0;
        if (totalIncome > 0) {
            double expenseRatio = (totalExpenses / totalIncome) * 100;
            if (expenseRatio > 70) {
                budgetAdherenceScore -= (expenseRatio - 70) * 0.5; // deduct points
                budgetAdherenceScore = Math.max(budgetAdherenceScore, 0);
            }
        } else {
            budgetAdherenceScore = 0;
        }

        double activeGoalsPct = 0.0;
        List<Goal> activeGoals = goalRepo.findByUserUserId(userId);
        if (!activeGoals.isEmpty()) {
            double sumPct = 0.0;
            for (Goal g : activeGoals) {
                sumPct += g.getTargetAmount() > 0 ? (g.getSavedAmount() / g.getTargetAmount()) * 100 : 0;
            }
            activeGoalsPct = (sumPct / activeGoals.size()) * 0.20; // max 20 points
        } else {
            activeGoalsPct = 10.0; // neutral starting points if no goals exist
        }

        double investmentDiversityScore = 5.0; // defaults to cash/wallet
        boolean hasStock = !user.getInvestments().isEmpty();
        boolean hasGold = !user.getGoldHoldings().isEmpty();
        if (hasStock && hasGold) {
            investmentDiversityScore = 15.0;
        } else if (hasStock || hasGold) {
            investmentDiversityScore = 10.0;
        }

        double riskExposureScore = 15.0;
        double highRiskStockCount = user.getInvestments().stream()
                .filter(i -> i.getRiskPercent() >= 12.0) // high risk indicator
                .count();
        if (highRiskStockCount > 2) {
            riskExposureScore = 8.0; // excessive high-risk exposure
        }

        int score = (int) Math.round(savingsRatioScore + budgetAdherenceScore + activeGoalsPct + investmentDiversityScore + riskExposureScore);
        score = Math.min(Math.max(score, 0), 100);

        String level = score >= 80 ? "Excellent" : score >= 60 ? "Good" : score >= 40 ? "Fair" : "Needs Improvement";

        return Map.of(
                "score", score,
                "level", level,
                "savingsRate", Math.round(savingsRate * 10.0) / 10.0,
                "investmentRate", Math.round(investmentRate * 10.0) / 10.0,
                "totalIncome", totalIncome,
                "totalExpenses", totalExpenses,
                "totalInvested", totalInvested,
                "walletBalance", user.getWalletBalance()
        );
    }

    // GOALS AND SAVINGS RULES
    @Transactional
    public GoalDto createGoal(GoalDto dto) throws Exception {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new Exception("User not found"));
        Goal goal = new Goal();
        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setSavedAmount(0.0);
        goal.setTargetDate(dto.getTargetDate());
        goal.setStatus("ACTIVE");
        goal.setIcon(dto.getIcon());
        goal.setUser(user);
        Goal saved = goalRepo.save(goal);

        // Dynamically compute progress based on current savings
        recalculateGoalProgress(user);

        return mapGoal(saved, dto.getUserId());
    }

    @Transactional
    public List<GoalDto> getGoalsByUser(Long userId) throws Exception {
        User user = userRepo.findById(userId).orElseThrow(() -> new Exception("User not found"));
        // Make sure we calculate dynamic progress before returning
        recalculateGoalProgress(user);
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
        if (newSaved >= goal.getTargetAmount()) goal.setStatus("Completed");
        else goal.setStatus("In Progress");
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
            userRepo.save(user);
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
    public void recalculateGoalProgress(User user) {
        double totalIncome = incomeLogRepo.findByUserUserId(user.getUserId()).stream()
                .mapToDouble(income -> income.getProfit() != null ? income.getProfit() : income.getAmount())
                .sum();

        double totalExpenses = user.getExpenses().stream()
                .filter(e -> !List.of("Salary", "Freelance", "Business", "Rental", "Other", "Stock_Sale").contains(e.getCategory()))
                .mapToDouble(Expense::getAmount).sum();

        double totalSavings = totalIncome - totalExpenses;
        if (totalSavings < 0) totalSavings = 0;

        List<Goal> goals = goalRepo.findByUserUserId(user.getUserId());
        double remainingSavings = totalSavings;

        for (Goal goal : goals) {
            double allocated = Math.min(remainingSavings, goal.getTargetAmount());
            goal.setSavedAmount(allocated);
            if (allocated >= goal.getTargetAmount()) {
                goal.setStatus("Completed");
            } else if (allocated > 0) {
                goal.setStatus("In Progress");
            } else {
                goal.setStatus("Active");
            }
            goalRepo.save(goal);
            remainingSavings -= allocated;
        }
    }

    // RECOMMENDATIONS
    public RecommendationDto getRecommendations(Double amount, String risk) {
        RecommendationDto dto = new RecommendationDto();
        dto.setRiskCategory(risk);
        dto.setTargetAmount(amount);

        List<String> symbols = new ArrayList<>();

        if ("LOW".equalsIgnoreCase(risk)) {
            dto.setGoldPct(60.0);
            dto.setStockPct(30.0);
            dto.setSavingsPct(10.0);

            symbols.addAll(List.of("RELIANCE", "HDFCBANK", "SBIN"));
        } else if ("MEDIUM".equalsIgnoreCase(risk)) {
            dto.setGoldPct(20.0);
            dto.setStockPct(50.0);
            dto.setSavingsPct(30.0);

            symbols.addAll(List.of("TCS", "INFY", "TATAMOTORS"));
        } else {
            // HIGH Risk
            dto.setGoldPct(10.0);
            dto.setStockPct(60.0);
            dto.setSavingsPct(30.0);

            symbols.addAll(List.of("ZOMATO", "ADANIPORTS", "WIPRO"));
        }

        dto.setGoldAllocatedAmount(Math.round((amount * dto.getGoldPct() / 100) * 100.0) / 100.0);
        dto.setStockAllocatedAmount(Math.round((amount * dto.getStockPct() / 100) * 100.0) / 100.0);
        dto.setSavingsAllocatedAmount(Math.round((amount * dto.getSavingsPct() / 100) * 100.0) / 100.0);
        dto.setRecommendedStocks(symbols);

        return dto;
    }

    public Map<String, Object> getGoldMarketPrices() {
        double basePrice = Double.parseDouble(configRepo.findById("gold_price")
                .map(SystemConfig::getConfigValue).orElse("7200.0"));
        return Map.of(
                "Digital", Math.round(basePrice * 100.0) / 100.0,
                "ETF",     Math.round(basePrice * 0.98 * 100.0) / 100.0,
                "SGB",     Math.round(basePrice * 0.97 * 100.0) / 100.0,
                "updatedAt", java.time.LocalDateTime.now().toString()
        );
    }

    public Map<String, Object> getRiskProfile(Long userId) throws Exception {
        User user = userRepo.findById(userId).orElseThrow(() -> new Exception("User not found"));
        double totalInvested = user.getInvestments().stream()
                .mapToDouble(Investment::getInvestedAmount).sum();
        double goldInvested = user.getGoldHoldings().stream()
                .mapToDouble(g -> g.getQuantityGrams() * g.getPurchasePricePerGram()).sum();
        double totalAssets = totalInvested + goldInvested + user.getWalletBalance();

        double avgStockRisk = user.getInvestments().isEmpty() ? 0 :
                user.getInvestments().stream().mapToDouble(Investment::getRiskPercent).average().orElse(0);
        double goldRisk = goldInvested > 0 ? 2.0 : 0.0; // gold is low risk
        double portfolioRiskPct = totalAssets > 0 ?
                ((avgStockRisk * totalInvested) + (goldRisk * goldInvested)) / totalAssets : 0;
        portfolioRiskPct = Math.round(portfolioRiskPct * 100.0) / 100.0;

        String riskLevel = portfolioRiskPct < 3 ? "LOW" : portfolioRiskPct < 7 ? "MEDIUM" : "HIGH";

        List<String> recommendations = new ArrayList<>();
        if (portfolioRiskPct > 8) {
            recommendations.add("⚠️ Your portfolio is high risk. Consider selling some high-risk stocks and investing in Digital Gold or SGB.");
        }
        if (user.getInvestments().isEmpty()) {
            recommendations.add("💡 You have no stock investments. Use the Smart Advisor to start building your portfolio.");
        }
        if (goldInvested == 0) {
            recommendations.add("🥇 Add some Digital Gold or SGB to diversify and reduce overall risk.");
        }
        if (user.getWalletBalance() > totalAssets * 0.5 && totalAssets > 0) {
            recommendations.add("💰 Over 50% of your wealth is idle in your wallet. Consider investing more.");
        }
        if (portfolioRiskPct < 2 && totalInvested > 0) {
            recommendations.add("📈 Your portfolio is very conservative. Consider adding some MEDIUM risk stocks for better returns.");
        }
        if (recommendations.isEmpty()) {
            recommendations.add("✅ Your portfolio looks well-balanced. Keep monitoring and rebalancing quarterly.");
        }

        List<String> goalNames = goalRepo.findByUserUserId(userId).stream()
                .map(Goal::getName).toList();

        return Map.of(
                "portfolioRiskPct", portfolioRiskPct,
                "riskLevel", riskLevel,
                "totalStockInvested", totalInvested,
                "totalGoldInvested", goldInvested,
                "walletBalance", user.getWalletBalance(),
                "totalAssets", totalAssets,
                "avgStockRiskPct", Math.round(avgStockRisk * 100.0) / 100.0,
                "recommendations", recommendations,
                "activeGoals", goalNames
        );
    }

    public Map<String, Object> getWalletBalance(Long userId) throws Exception {
        User user = userRepo.findById(userId).orElseThrow(() -> new Exception("User not found"));
        return Map.of("walletBalance", user.getWalletBalance(), "userId", userId);
    }
}
