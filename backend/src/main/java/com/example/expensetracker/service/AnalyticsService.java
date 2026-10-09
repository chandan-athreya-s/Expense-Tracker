package com.example.expensetracker.service;

import com.example.expensetracker.dto.SpendingSummaryResponse;
import com.example.expensetracker.exception.BadRequestException;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service 
@Transactional(readOnly = true)
public class AnalyticsService {
    private final ExpenseRepository expenseRepository;

    public AnalyticsService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public SpendingSummaryResponse getSummary(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("StartDate must be after EndDate");

        }

        BigDecimal total = expenseRepository.sumAmountBetween(startDate, endDate);

        if (total == null) {
            total = BigDecimal.ZERO;
        }

        long count = expenseRepository.countByExpenseDateBetween(startDate, endDate);

        return new SpendingSummaryResponse(startDate, endDate, total, count);
    }
}
