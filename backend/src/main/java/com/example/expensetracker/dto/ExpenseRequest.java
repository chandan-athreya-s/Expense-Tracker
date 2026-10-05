package com.example.expensetracker.dto;

import com.example.expensetracker.entity.PaymentMethod;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest (
    @NotNull(message = "Date is required")
    LocalDate date,

    @NotNull(message = "Category is required")
    Long categoryId,

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "amount can have at most 2 decimal places")
    BigDecimal amount,

    @Size(max = 255, message = "description can be at most 255 characters")
    String description,

    @NotBlank(message = "Payment method is required")
    PaymentMethod paymentMethod

) {}
