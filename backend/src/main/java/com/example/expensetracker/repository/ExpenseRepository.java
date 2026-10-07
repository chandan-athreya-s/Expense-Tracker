package com.example.expensetracker.repository;

import com.example.expensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Sort;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
   List<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate, Sort sort); 
}
