package com.example.expensetracker.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.expensetracker.dto.CategoryTotalResponse;
import com.example.expensetracker.dto.DailyTotalResponse;
import com.example.expensetracker.entity.Expense;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ExpenseRepository extends JpaRepository<Expense, Long>,
                JpaSpecificationExecutor<Expense> {
        @Query("""
                        SELECT SUM(e.amount)
                        FROM Expense e
                        WHERE e.expenseDate BETWEEN :startDate AND :endDate
                        """)
        BigDecimal sumAmountBetween(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        long countByExpenseDateBetween(LocalDate startDate, LocalDate endDate);

        @Query("""
                        SELECT new com.example.expensetracker.dto.CategoryTotalResponse(
                                    c.id, c.name, SUM(e.amount))
                        FROM Expense e
                        JOIN e.category c
                        WHERE e.expenseDate BETWEEN :startDate AND :endDate
                        GROUP BY c.id, c.name
                        ORDER BY SUM(e.amount) DESC
                        """)
        List<CategoryTotalResponse> findCategoryTotals(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("""
                        SELECT new com.example.expensetracker.dto.DailyTotalResponse(
                                        e.expenseDate, SUM(e.amount))
                        FROM Expense e
                        WHERE e.expenseDate BETWEEN :startDate AND :endDate
                        GROUP BY e.expenseDate
                        ORDER BY e.expenseDate ASC

                                """)
        List<DailyTotalResponse> findDailyTotals(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Override 
        @EntityGraph(attributePaths = "category")
        List<Expense> findAll(Specification<Expense> spec, Sort sort);

        @Override
        @EntityGraph(attributePaths = "category")
        Page<Expense> findAll(Specification<Expense> spec, Pageable pageable);

        @Override 
        @EntityGraph(attributePaths = "category")
        Optional<Expense> findById(Long id);
}
