package com.example.expensetracker.service;

import com.example.expensetracker.dto.BudgetResponse;
import com.example.expensetracker.dto.BudgetStatusResponse;
import com.example.expensetracker.dto.SpendingSummaryResponse;
import com.example.expensetracker.entity.Budget;
import com.example.expensetracker.exception.BadRequestException;
import com.example.expensetracker.exception.ResourceNotFoundException;
import com.example.expensetracker.repository.BudgetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private BudgetService budgetService;

    @Test
    void setBudgetCreatesBudgetWhenOneDoesNotExist() {
        BigDecimal amount = new BigDecimal("40000.00");
        when(budgetRepository.findByYearAndMonth(2026, 10)).thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BudgetResponse result = budgetService.setBudget(2026, 10, amount);

        assertEquals(2026, result.year());
        assertEquals(10, result.month());
        assertMoney("40000.00", result.amount());
        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    void setBudgetUpdatesExistingBudgetInsteadOfCreatingAnother() {
        Budget existing = new Budget(2026, 10, new BigDecimal("30000.00"));
        BigDecimal updatedAmount = new BigDecimal("40000.00");
        when(budgetRepository.findByYearAndMonth(2026, 10)).thenReturn(Optional.of(existing));
        when(budgetRepository.save(any(Budget.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BudgetResponse result = budgetService.setBudget(2026, 10, updatedAmount);

        assertMoney("40000.00", existing.getAmount());
        assertMoney("40000.00", result.amount());
        verify(budgetRepository).save(existing);
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    void getBudgetThrowsWhenNoBudgetExists() {
        when(budgetRepository.findByYearAndMonth(2026, 10)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> budgetService.getBudget(2026, 10));
    }

    @Test
    void getStatusWithoutBudgetReturnsSpentAndNullBudgetFields() {
        stubMonthlySpent(2026, 11, "0.00");
        when(budgetRepository.findByYearAndMonth(2026, 11)).thenReturn(Optional.empty());

        BudgetStatusResponse result = budgetService.getStatus(2026, 11);

        assertFalse(result.budgetSet());
        assertNull(result.budget());
        assertNull(result.remaining());
        assertNull(result.percentUsed());
        assertMoney("0.00", result.totalSpent());
        assertFalse(result.exceeded());
    }

    @Test
    void getStatusCalculatesRemainingAndPercentageWhenUnderBudget() {
        when(budgetRepository.findByYearAndMonth(2026, 10))
                .thenReturn(Optional.of(new Budget(2026, 10, new BigDecimal("40000.00"))));
        stubMonthlySpent(2026, 10, "20000.00");

        BudgetStatusResponse result = budgetService.getStatus(2026, 10);

        assertTrue(result.budgetSet());
        assertMoney("40000.00", result.budget());
        assertMoney("20000.00", result.totalSpent());
        assertMoney("20000.00", result.remaining());
        assertMoney("50.0", result.percentUsed());
        assertFalse(result.exceeded());
    }

    @Test
    void getStatusReportsNegativeRemainingWhenOverBudget() {
        when(budgetRepository.findByYearAndMonth(2026, 10))
                .thenReturn(Optional.of(new Budget(2026, 10, new BigDecimal("40000.00"))));
        stubMonthlySpent(2026, 10, "51325.00");

        BudgetStatusResponse result = budgetService.getStatus(2026, 10);

        assertMoney("-11325.00", result.remaining());
        assertMoney("128.3", result.percentUsed());
        assertTrue(result.exceeded());
    }

    @Test
    void getStatusDoesNotMarkBudgetExceededWhenSpendingEqualsBudget() {
        when(budgetRepository.findByYearAndMonth(2026, 10))
                .thenReturn(Optional.of(new Budget(2026, 10, new BigDecimal("40000.00"))));
        stubMonthlySpent(2026, 10, "40000.00");

        BudgetStatusResponse result = budgetService.getStatus(2026, 10);

        assertMoney("0.00", result.remaining());
        assertMoney("100.0", result.percentUsed());
        assertFalse(result.exceeded());
    }

    @Test
    void setBudgetRejectsMonthBelowOne() {
        assertThrows(BadRequestException.class,
                () -> budgetService.setBudget(2026, 0, new BigDecimal("100.00")));
        verifyNoInteractions(budgetRepository);
    }

    @Test
    void getStatusRejectsMonthAboveTwelve() {
        assertThrows(BadRequestException.class, () -> budgetService.getStatus(2026, 13));
        verifyNoInteractions(budgetRepository, analyticsService);
    }

    @Test
    void getBudgetRejectsYearOutsideSupportedRange() {
        assertThrows(BadRequestException.class, () -> budgetService.getBudget(1999, 10));
        verifyNoInteractions(budgetRepository);
    }

    private void stubMonthlySpent(int year, int month, String amount) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        when(analyticsService.getSummary(start, end)).thenReturn(
                new SpendingSummaryResponse(start, end, new BigDecimal(amount), 2L));
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        assertNotNull(actual);
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
