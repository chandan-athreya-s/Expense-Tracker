package com.example.expensetracker.service;

import com.example.expensetracker.dto.CategoryTotalResponse;
import com.example.expensetracker.dto.DailyTotalResponse;
import com.example.expensetracker.dto.SpendingSummaryResponse;
import com.example.expensetracker.exception.BadRequestException;
import com.example.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void getSummaryReturnsTotalAndCountForRequestedRange() {
        LocalDate start = LocalDate.of(2026, 10, 1);
        LocalDate end = LocalDate.of(2026, 10, 31);
        when(expenseRepository.sumAmountBetween(start, end)).thenReturn(new BigDecimal("385.00"));
        when(expenseRepository.countByExpenseDateBetween(start, end)).thenReturn(6L);

        SpendingSummaryResponse result = analyticsService.getSummary(start, end);

        assertEquals(start, result.startDate());
        assertEquals(end, result.endDate());
        assertMoney("385.00", result.totalSpent());
        assertEquals(6L, result.expenseCount());
        verify(expenseRepository).sumAmountBetween(start, end);
        verify(expenseRepository).countByExpenseDateBetween(start, end);
    }

    @Test
    void getSummaryConvertsNullSumToZeroForEmptyRange() {
        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 30);
        when(expenseRepository.sumAmountBetween(start, end)).thenReturn(null);
        when(expenseRepository.countByExpenseDateBetween(start, end)).thenReturn(0L);

        SpendingSummaryResponse result = analyticsService.getSummary(start, end);

        assertMoney("0", result.totalSpent());
        assertEquals(0L, result.expenseCount());
    }

    @Test
    void getSummaryRejectsReversedDateRange() {
        LocalDate start = LocalDate.of(2026, 10, 31);
        LocalDate end = LocalDate.of(2026, 10, 1);

        assertThrows(BadRequestException.class, () -> analyticsService.getSummary(start, end));
        verifyNoInteractions(expenseRepository);
    }

    @Test
    void getCategoryTotalsReturnsRepositoryRowsInRepositoryOrder() {
        LocalDate start = LocalDate.of(2026, 10, 1);
        LocalDate end = LocalDate.of(2026, 10, 31);
        List<CategoryTotalResponse> rows = List.of(
                new CategoryTotalResponse(1L, "Rent", new BigDecimal("13000.00")),
                new CategoryTotalResponse(2L, "Refreshments", new BigDecimal("850.00")));
        when(expenseRepository.findCategoryTotals(start, end)).thenReturn(rows);

        List<CategoryTotalResponse> result = analyticsService.getCategoryTotals(start, end);

        assertEquals(rows, result);
        assertMoney("13000.00", result.get(0).totalSpent());
        assertMoney("850.00", result.get(1).totalSpent());
        verify(expenseRepository).findCategoryTotals(start, end);
    }

    @Test
    void getCategoryTotalsReturnsEmptyListWhenThereAreNoMatchingExpenses() {
        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 30);
        when(expenseRepository.findCategoryTotals(start, end)).thenReturn(List.of());

        assertTrue(analyticsService.getCategoryTotals(start, end).isEmpty());
    }

    @Test
    void getCategoryTotalsRejectsReversedDateRange() {
        assertThrows(BadRequestException.class, () -> analyticsService.getCategoryTotals(
                LocalDate.of(2026, 10, 31), LocalDate.of(2026, 10, 1)));
        verifyNoInteractions(expenseRepository);
    }

    @Test
    void getDailyTotalsCombinesRepositoryRowsAndFillsMissingDaysWithZero() {
        LocalDate start = LocalDate.of(2026, 10, 1);
        LocalDate end = LocalDate.of(2026, 10, 3);
        when(expenseRepository.findDailyTotals(start, end)).thenReturn(List.of(
                new DailyTotalResponse(LocalDate.of(2026, 10, 1), new BigDecimal("25.00")),
                new DailyTotalResponse(LocalDate.of(2026, 10, 3), new BigDecimal("12.50"))));

        List<DailyTotalResponse> result = analyticsService.getDailyTotals(start, end);

        assertEquals(3, result.size());
        assertEquals(start, result.get(0).expenseDate());
        assertMoney("25.00", result.get(0).totalSpent());
        assertEquals(LocalDate.of(2026, 10, 2), result.get(1).expenseDate());
        assertMoney("0", result.get(1).totalSpent());
        assertEquals(end, result.get(2).expenseDate());
        assertMoney("12.50", result.get(2).totalSpent());
    }

    @Test
    void getDailyTotalsReturnsChronologicalRowsAndIncludesBothEndpoints() {
        LocalDate start = LocalDate.of(2026, 10, 10);
        LocalDate end = LocalDate.of(2026, 10, 11);
        when(expenseRepository.findDailyTotals(start, end)).thenReturn(List.of(
                new DailyTotalResponse(end, new BigDecimal("20.00")),
                new DailyTotalResponse(start, new BigDecimal("10.00"))));

        List<DailyTotalResponse> result = analyticsService.getDailyTotals(start, end);

        assertEquals(2, result.size());
        assertEquals(start, result.get(0).expenseDate());
        assertEquals(end, result.get(1).expenseDate());
    }

    @Test
    void getDailyTotalsAllowsExactly366Days() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 12, 31); // 366 inclusive days in a leap year
        when(expenseRepository.findDailyTotals(start, end)).thenReturn(List.of());

        List<DailyTotalResponse> result = analyticsService.getDailyTotals(start, end);

        assertEquals(366, result.size());
        assertTrue(result.stream().allMatch(day -> day.totalSpent().compareTo(BigDecimal.ZERO) == 0));
    }

    @Test
    void getDailyTotalsRejectsRangesLongerThan366Days() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2027, 1, 2); // 367 inclusive days

        assertThrows(BadRequestException.class, () -> analyticsService.getDailyTotals(start, end));
        verifyNoInteractions(expenseRepository);
    }

    @Test
    void getDailyTotalsRejectsReversedDateRange() {
        assertThrows(BadRequestException.class, () -> analyticsService.getDailyTotals(
                LocalDate.of(2026, 10, 31), LocalDate.of(2026, 10, 1)));
        verifyNoInteractions(expenseRepository);
    }

    @Test
    void categoryAndDailyTotalsCanBeCrossCheckedAgainstSummary() {
        LocalDate start = LocalDate.of(2026, 10, 1);
        LocalDate end = LocalDate.of(2026, 10, 2);
        when(expenseRepository.findCategoryTotals(start, end)).thenReturn(List.of(
                new CategoryTotalResponse(1L, "Rent", new BigDecimal("100.00")),
                new CategoryTotalResponse(2L, "Food", new BigDecimal("25.00"))));
        when(expenseRepository.findDailyTotals(start, end)).thenReturn(List.of(
                new DailyTotalResponse(start, new BigDecimal("100.00")),
                new DailyTotalResponse(end, new BigDecimal("25.00"))));
        when(expenseRepository.sumAmountBetween(start, end)).thenReturn(new BigDecimal("125.00"));
        when(expenseRepository.countByExpenseDateBetween(start, end)).thenReturn(2L);

        BigDecimal categorySum = analyticsService.getCategoryTotals(start, end).stream()
                .map(CategoryTotalResponse::totalSpent).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal dailySum = analyticsService.getDailyTotals(start, end).stream()
                .map(DailyTotalResponse::totalSpent).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal summarySum = analyticsService.getSummary(start, end).totalSpent();

        assertMoney("125.00", categorySum);
        assertMoney("125.00", dailySum);
        assertMoney("125.00", summarySum);
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        assertNotNull(actual);
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
