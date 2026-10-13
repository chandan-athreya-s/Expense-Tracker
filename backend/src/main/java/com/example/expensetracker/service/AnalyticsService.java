package com.example.expensetracker.service;
import com.example.expensetracker.dto.CategoryTotalResponse;
import com.example.expensetracker.dto.SpendingSummaryResponse;
import com.example.expensetracker.exception.BadRequestException;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.dto.DailyTotalResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service 
@Transactional(readOnly = true)
public class AnalyticsService {
    private final ExpenseRepository expenseRepository;
    private static final long MAX_DAILY_RANGE_DAYS = 366;

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

    public List<DailyTotalResponse> getDailyTotals(LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);

        long daysInRange = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (daysInRange > MAX_DAILY_RANGE_DAYS) {
            throw new BadRequestException(
                "Date range too large: maximum is " + MAX_DAILY_RANGE_DAYS + " days");
        }

        List<DailyTotalResponse> rows = expenseRepository.findDailyTotals(startDate, endDate);

        Map<LocalDate, BigDecimal> totalsByDate = rows.stream()
                .collect(Collectors.toMap(DailyTotalResponse::expenseDate, DailyTotalResponse::totalSpent));

        return startDate.datesUntil(endDate.plusDays(1))
                .map(day -> new DailyTotalResponse(day, totalsByDate.getOrDefault(day, BigDecimal.ZERO)))
                .toList();
    }

}
