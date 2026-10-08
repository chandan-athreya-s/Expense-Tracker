package com.example.expensetracker.dto;

import com.example.expensetracker.entity.PaymentMethod;
import java.time.LocalDate;

public record ExpenseFilter(
    LocalDate startDate,
    LocalDate endDate,
    Long categoryId,
    PaymentMethod paymentMethod
) { } 