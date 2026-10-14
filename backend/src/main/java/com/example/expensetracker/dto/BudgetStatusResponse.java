package com.example.expensetracker.dto;

import java.math.BigDecimal;

public record BudgetStatusResponse(
    int year,
    int month,
    boolean budgetSet,
    BigDecimal budget,
    BigDecimal totalSpent,
    BigDecimal remaining,
    BigDecimal percentUsed,
    boolean exceeded
) {}
