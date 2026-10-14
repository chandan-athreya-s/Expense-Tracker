package com.example.expensetracker.dto;

import java.math.BigDecimal;

public record BudgetResponse(int year, int month, BigDecimal amount) {}
