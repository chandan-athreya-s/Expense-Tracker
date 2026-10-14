package com.example.expensetracker.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BudgetRequest(
    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "amount must have at most 10 digits and 2 decimals")
    BigDecimal amount
) {}
