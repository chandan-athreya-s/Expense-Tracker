package com.example.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
public record DailyTotalResponse(
        LocalDate expenseDate,
        BigDecimal totalSpent
) {}
