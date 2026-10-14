package com.example.expensetracker.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
    name = "budgets",

    uniqueConstraints = @UniqueConstraint(
        name = "uk_budgets_year_month",
        columnNames = {"budget_year", "budget_month"})
)
public class Budget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "budget_year", nullable = false)
    private int year;

    @Column(name = "budget_month", nullable = false)
    private int month;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    protected Budget() { }

    public Budget(int year, int month, BigDecimal amount) {
        this.year = year;
        this.month = month;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public int getYear() { return year; }
    public int getMonth() { return month; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
