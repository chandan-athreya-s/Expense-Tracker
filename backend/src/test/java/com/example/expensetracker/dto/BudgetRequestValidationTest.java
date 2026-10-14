package com.example.expensetracker.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BudgetRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void rejectsNullAmount() {
        BudgetRequest request = new BudgetRequest(null);

        assertTrue(validator.validate(request).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void rejectsZeroOrNegativeAmount() {
        assertFalse(validator.validate(new BudgetRequest(BigDecimal.ZERO)).isEmpty());
        assertFalse(validator.validate(new BudgetRequest(new BigDecimal("-1.00"))).isEmpty());
    }

    @Test
    void acceptsPositiveAmountWithAtMostTwoDecimalPlaces() {
        BudgetRequest request = new BudgetRequest(new BigDecimal("1234.56"));

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsAmountWithMoreThanTwoDecimalPlaces() {
        BudgetRequest request = new BudgetRequest(new BigDecimal("1234.567"));

        assertFalse(validator.validate(request).isEmpty());
    }
}
