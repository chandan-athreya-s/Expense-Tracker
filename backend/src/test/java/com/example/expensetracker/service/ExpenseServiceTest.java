package com.example.expensetracker.service;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseResponse;
import com.example.expensetracker.entity.Category;
import com.example.expensetracker.entity.PaymentMethod;
import com.example.expensetracker.exception.BadRequestException;          // NEW
import com.example.expensetracker.exception.ResourceNotFoundException;
import com.example.expensetracker.repository.CategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.example.expensetracker.dto.ExpenseFilter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;                                                     // NEW

import static org.junit.jupiter.api.Assertions.*;

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

    // NEW helper: create a 10.00 UPI expense on the given date (format yyyy-MM-dd).
    private void createOn(Category category, String date) {
        expenseService.create(new ExpenseRequest(LocalDate.parse(date), category.getId(),
                new BigDecimal("10.00"), "x", PaymentMethod.UPI));
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

    // NEW: the boundary test. Off-by-one mistakes on the first and last day of a month
    // are the classic reporting bug, so we put an expense just outside each end.
    @Test
    void getAllWithDateRangeIncludesBothEndsAndExcludesOutside() {
        Category category = newCategory();
        createOn(category, "2026-09-30");   // just before the range: excluded
        createOn(category, "2026-10-01");   // first day: included
        createOn(category, "2026-10-31");   // last day: included
        createOn(category, "2026-11-01");   // just after the range: excluded

        List<ExpenseResponse> october =
                expenseService.getAll(new ExpenseFilter(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), null, null));

        assertEquals(2, october.size());
        // Results are newest first, so Oct 31 must come before Oct 1.
        assertEquals(LocalDate.of(2026, 10, 31), october.get(0).expenseDate());
    }

    // NEW: the two invalid shapes the service must reject.
    @Test
    void getAllWithOnlyOneDateOrReversedRangeThrowsBadRequest() {
        // Only one of the two dates supplied.
        assertThrows(BadRequestException.class,
                () -> expenseService.getAll(new ExpenseFilter(LocalDate.of(2026, 10, 1), null, null, null)));
        // Start date after end date.
        assertThrows(BadRequestException.class,
                () -> expenseService.getAll(new ExpenseFilter(LocalDate.of(2026, 10, 31), LocalDate.of(2026, 10, 1), null, null)));
    }

    @Test
    void getAllFilterByCategoryAndPaymentMethod() {
        Category a = newCategory();
        Category b = categoryRepository.save(new Category("Second Test Category"));

        createOn(a, "2026-10-01");
        expenseService.create(new ExpenseRequest(LocalDate.of(2026, 10, 2), a.getId(),
                new BigDecimal("10.00"), "x", PaymentMethod.CASH));
        createOn(b, "2026-10-03");

        assertEquals(3, expenseService.getAll(new ExpenseFilter(null, null, null, null)).size());
        assertEquals(2, expenseService.getAll(new ExpenseFilter(null, null, a.getId(), null)).size());
        assertEquals(1, expenseService.getAll(new ExpenseFilter(null, null, null, PaymentMethod.CASH)).size());
        assertEquals(1, expenseService.getAll(new ExpenseFilter(null, null, a.getId(), PaymentMethod.UPI)).size());
        assertEquals(0, expenseService.getAll(new ExpenseFilter(null, null, b.getId(), PaymentMethod.CASH)).size());
    }
}