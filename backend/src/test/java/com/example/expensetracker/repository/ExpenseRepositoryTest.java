package com.example.expensetracker.repository;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase; 

import com.example.expensetracker.entity.Category;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.PaymentMethod;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class ExpenseRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @PersistenceContext 
    private EntityManager entityManager;

    @Test
    void savesAnExpenseAndReadsItBackUnchanged() {
        Category food = categoryRepository.save(new Category("Food"));
        
        Expense expense = new Expense();
        expense.setExpenseDate(LocalDate.of(2026, 10, 4));
        expense.setCategory(food);
        expense.setAmount(new BigDecimal("120.00"));
        expense.setDescription("Lunch");
        expense.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        Expense saved = expenseRepository.save(expense);

        entityManager.flush();
        entityManager.clear();

        Expense found = expenseRepository.findById(saved.getId()).orElseThrow();

        assertEquals(LocalDate.of(2026, 10, 4), found.getExpenseDate());
        assertEquals("Lunch", found.getDescription());
        assertEquals(PaymentMethod.CREDIT_CARD, found.getPaymentMethod());
        assertEquals("Food", found.getCategory().getName());
        
        assertEquals(0, new BigDecimal("120.00").compareTo(found.getAmount()));

        assertNotNull(found.getCreatedAt());
        assertNotNull(found.getUpdatedAt());
    }

    @Test
    void findsCategoryByNameThroughDerivedQuery() {
        categoryRepository.save(new Category("Travel"));

        assertTrue(categoryRepository.existsByName("Travel"));
        assertFalse(categoryRepository.existsByName("Does Not Exist"));
    }
}
