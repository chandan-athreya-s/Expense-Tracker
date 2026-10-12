package com.example.expensetracker.dto;

import java.math.BigDecimal;
public record CategoryTotalResponse(
        Long categoryId,
        String categoryName,
        BigDecimal totalSpent
) {}