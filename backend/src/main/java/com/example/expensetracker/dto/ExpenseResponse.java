package com.example.expensetracker.dto;

import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ExpenseResponse (
    Long id,
    LocalDate expenseDate,
    Long categoryId,
    String categoryName,
    BigDecimal amount,
    String description,
    PaymentMethod paymentMethod,
    Instant createdAt,
    Instant updatedAt

) { 
    public static ExpenseResponse from(Expense expense){
        return new ExpenseResponse(
            expense.getId(),
            expense.getExpenseDate(),
            expense.getCategory().getId(),
            expense.getCategory().getName(),
            expense.getAmount(),
            expense.getDescription(),
            expense.getPaymentMethod(),
            expense.getCreatedAt(),
            expense.getUpdatedAt()
        );
    }
}
