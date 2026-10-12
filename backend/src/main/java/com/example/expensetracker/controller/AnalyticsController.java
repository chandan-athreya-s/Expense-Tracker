package com.example.expensetracker.controller;

import com.example.expensetracker.dto.SpendingSummaryResponse;
import com.example.expensetracker.service.AnalyticsService;
import com.example.expensetracker.dto.CategoryTotalResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;
@RestController 
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/category-totals")
    public List<CategoryTotalResponse> getCategoryTotals(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
                return analyticsService.getCategoryTotals(startDate, endDate);
            }

    @GetMapping("/summary")
    public SpendingSummaryResponse getSummary(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    
    return analyticsService.getSummary(startDate, endDate);
        }
}
