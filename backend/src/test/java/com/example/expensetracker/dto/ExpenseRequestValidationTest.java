package com.example.expensetracker.dto;

import com.example.expensetracker.entity.PaymentMethod;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpenseRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validRequestHasNoViolations() {
        ExpenseRequest valid = new ExpenseRequest(
                LocalDate.of(2026, 10, 1), 2L, new BigDecimal("120.00"), null, PaymentMethod.UPI);

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(valid);

        assertTrue(violations.isEmpty());
    }

    @Test
    void missingRequiredFieldsAndNegativeAmountAreAllReported() {
        ExpenseRequest invalid = new ExpenseRequest(null, null, new BigDecimal("-5"), null, null);

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(invalid);

        assertEquals(4, violations.size());
    }
}