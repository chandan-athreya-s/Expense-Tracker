package com.example.expensetracker.service;

import com.example.expensetracker.dto.BudgetResponse;
import com.example.expensetracker.dto.BudgetStatusResponse;
import com.example.expensetracker.entity.Budget;
import com.example.expensetracker.exception.BadRequestException;
import com.example.expensetracker.exception.ResourceNotFoundException;
import com.example.expensetracker.repository.BudgetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

@Service 
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final AnalyticsService analyticsService;

    public BudgetService(BudgetRepository budgetRepository, AnalyticsService analyticsService) {
        this.budgetRepository = budgetRepository;
        this.analyticsService = analyticsService;
    }

    @Transactional
    public BudgetResponse setBudget(int year, int month, BigDecimal amount) {
        validateYearMonth(year, month);

        Budget budget = budgetRepository.findByYearAndMonth(year, month)
                .map(existing -> {
                    existing.setAmount(amount);
                    return existing;
                })
                .orElseGet(() -> new Budget(year, month, amount));

            Budget saved = budgetRepository.save(budget);
            return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public BudgetResponse getBudget(int year, int month) {
        validateYearMonth(year, month);
        Budget budget = budgetRepository.findByYearAndMonth(year, month)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No budget set for " + year + "-" + String.format("%02d", month)
                ));
        return toResponse(budget);
    }

    @Transactional(readOnly = true)
    public BudgetStatusResponse getStatus(int year, int month) {
        validateYearMonth(year, month);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        BigDecimal spent = analyticsService.getSummary(start, end).totalSpent();

        return budgetRepository.findByYearAndMonth(year, month)
                .map(b -> buildStatus(year, month, b.getAmount(), spent))
                .orElseGet(() -> new BudgetStatusResponse(
                        year, month, false, null, spent, null, null, false));
    }

    private BudgetStatusResponse buildStatus(int year, int month, BigDecimal budget, BigDecimal spent) {
        BigDecimal remaining = budget.subtract(spent);
        
        BigDecimal percentUsed = spent
                .multiply(BigDecimal.valueOf(100))
                .divide(budget, 1, RoundingMode.HALF_UP);

        boolean exceeded = spent.compareTo(budget) > 0;

        return new BudgetStatusResponse(year, month, true, budget, spent, remaining, percentUsed,exceeded);
    }

    private void validateYearMonth(int year, int month) {
        if (month < 1 || month > 12) {
            throw new BadRequestException("month must be between 1 and 12");
        }
        if (year < 2000 || year > 2100) {
            throw new BadRequestException("year must be between 2000 and 2100");
        }
    }

    private BudgetResponse toResponse(Budget b) {
        return new BudgetResponse(b.getYear(), b.getMonth(), b.getAmount());
    }
}
