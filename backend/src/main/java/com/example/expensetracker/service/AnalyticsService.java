package com.example.expensetracker.service;
import com.example.expensetracker.dto.CategoryTotalResponse;
import com.example.expensetracker.dto.SpendingSummaryResponse;
import com.example.expensetracker.exception.BadRequestException;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service 
@Transactional(readOnly = true)
public class AnalyticsService {
    private final ExpenseRepository expenseRepository;

    public AnalyticsService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    private void validateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("StartDate must not be after EndDate");

        }
    }

    public SpendingSummaryResponse getSummary(LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);

        BigDecimal total = expenseRepository.sumAmountBetween(startDate, endDate);

        if (total == null) {
            total = BigDecimal.ZERO;
        }

        long count = expenseRepository.countByExpenseDateBetween(startDate, endDate);

        return new SpendingSummaryResponse(startDate, endDate, total, count);
    }

    public List<CategoryTotalResponse> getCategoryTotals(LocalDate startDate, LocalDate endDate) {
            validateRange(startDate, endDate);
            return expenseRepository.findCategoryTotals(startDate, endDate);
        }

}
