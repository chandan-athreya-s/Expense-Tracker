package com.example.expensetracker.service;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseResponse;
import com.example.expensetracker.entity.Category;
import com.example.expensetracker.entity.PaymentMethod;
import com.example.expensetracker.exception.ResourceNotFoundException;
import com.example.expensetracker.repository.CategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/*
 * @SpringBootTest starts the whole application context, so the real service, real
 * repositories and real PostgreSQL work together. We use the "test" profile so it
 * points at expense_tracker_test, never your real data.
 *
 * @Transactional on a TEST class makes every test roll back at the end, so tests
 * stay independent and leave nothing behind.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExpenseServiceTest {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private CategoryRepository categoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // Helper: the seeder already inserted real categories into the test DB, so we
    // create our own uniquely named one to avoid clashing with the unique-name rule.
    private Category newCategory() {
        return categoryRepository.save(new Category("Service Test Category"));
    }

    private ExpenseRequest requestFor(Category category, String amount, String description) {
        return new ExpenseRequest(LocalDate.of(2026, 10, 1), category.getId(),
                new BigDecimal(amount), description, PaymentMethod.UPI);
    }

    @Test
    void createReturnsSavedExpenseWithGeneratedIdAndCategoryName() {
        Category category = newCategory();

        ExpenseResponse response = expenseService.create(requestFor(category, "120.00", "Lunch"));

        assertNotNull(response.id());                       // the database generated it
        assertEquals("Service Test Category", response.categoryName());
        assertEquals(0, new BigDecimal("120.00").compareTo(response.amount()));
        assertNotNull(response.createdAt());
    }

    @Test
    void createWithUnknownCategoryThrowsNotFound() {
        ExpenseRequest request = new ExpenseRequest(LocalDate.of(2026, 10, 1), 999_999L,
                new BigDecimal("10.00"), "Ghost category", PaymentMethod.CASH);

        // assertThrows passes only if the code inside throws exactly this exception type.
        assertThrows(ResourceNotFoundException.class, () -> expenseService.create(request));
    }

    @Test
    void updateChangesTheStoredValues() {
        Category category = newCategory();
        Long id = expenseService.create(requestFor(category, "120.00", "Lunch")).id();

        ExpenseResponse updated = expenseService.update(id, requestFor(category, "150.00", "Big lunch"));

        assertEquals("Big lunch", updated.description());
        assertEquals(0, new BigDecimal("150.00").compareTo(updated.amount()));
    }

    @Test
    void deleteRemovesTheExpense() {
        Category category = newCategory();
        Long id = expenseService.create(requestFor(category, "20.00", "Chai")).id();

        expenseService.delete(id);
        entityManager.flush();   // push the DELETE to the database now
        entityManager.clear();   // forget cached objects so the next lookup hits the database

        assertThrows(ResourceNotFoundException.class, () -> expenseService.getById(id));
    }
}