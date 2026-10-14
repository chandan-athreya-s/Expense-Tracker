package com.example.expensetracker.controller;

import com.example.expensetracker.dto.BudgetRequest;
import com.example.expensetracker.dto.BudgetResponse;
import com.example.expensetracker.dto.BudgetStatusResponse;
import com.example.expensetracker.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PutMapping("/{year}/{month}")
    public BudgetResponse setBudget(@PathVariable int year,
            @PathVariable int month,
            @Valid @RequestBody BudgetRequest request) {
        return budgetService.setBudget(year, month, request.amount());
    }

    @GetMapping("/{year}/{month}")
    public BudgetResponse getBudget(@PathVariable int year, @PathVariable int month) {
        return budgetService.getBudget(year, month);
    }

    @GetMapping("/{year}/{month}/status")
    public BudgetStatusResponse getStatus(@PathVariable int year, @PathVariable int month) {
        return budgetService.getStatus(year, month);
    }
}
