package com.example.expensetracker.repository;

import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.PaymentMethod;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;


public final class ExpenseSpecifications {

    private ExpenseSpecifications() { }   // utility class: nobody should create instances

    // WHERE expense_date BETWEEN :start AND :end   (both ends included)
    public static Specification<Expense> dateBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> cb.between(root.<LocalDate>get("expenseDate"), start, end);
    }

    // WHERE category_id = :categoryId
    // root.get("category").get("id") reads the foreign-key column directly,
    // so no extra JOIN to the categories table is needed.
    public static Specification<Expense> hasCategory(Long categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    // WHERE payment_method = :method
    public static Specification<Expense> hasPaymentMethod(PaymentMethod method) {
        return (root, query, cb) -> cb.equal(root.get("paymentMethod"), method);
    }
}