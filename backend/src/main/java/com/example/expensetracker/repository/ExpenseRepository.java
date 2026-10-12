package com.example.expensetracker.repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.example.expensetracker.dto.CategoryTotalResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.expensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ExpenseRepository extends JpaRepository<Expense, Long>, 
JpaSpecificationExecutor <Expense> {
   @Query("""
           SELECT SUM(e.amount)
           FROM Expense e
           WHERE e.expenseDate BETWEEN :startDate AND :endDate
           """)
    BigDecimal sumAmountBetween(@Param("startDate") LocalDate startDate, 
                                @Param("endDate") LocalDate endDate);

    long countByExpenseDateBetween(LocalDate startDate, LocalDate endDate);
    @Query ("""
            SELECT new com.example.expensetracker.dto.CategoryTotalResponse(
                        c.id, c.name, SUM(e.amount))
            FROM Expense e
            JOIN e.category c
            WHERE e.expenseDate BETWEEN :startDate AND :endDate
            GROUP BY c.id, c.name
            ORDER BY SUM(e.amount) DESC
            """)
    List <CategoryTotalResponse> findCategoryTotals(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);
}
