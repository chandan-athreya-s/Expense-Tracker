package com.example.expensetracker.service;

import com.example.expensetracker.dto.ExpenseFilter;
import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseResponse;
import com.example.expensetracker.entity.Category;
import com.example.expensetracker.entity.PaymentMethod;
import com.example.expensetracker.exception.BadRequestException;
import com.example.expensetracker.exception.ResourceNotFoundException;
import com.example.expensetracker.repository.CategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExpenseServiceTest {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext
    private EntityManager entityManager;

    private Category newCategory() {
        return categoryRepository.save(new Category("Service Test Category"));
    }

    private ExpenseRequest requestFor(Category category, String amount, String description) {
        return new ExpenseRequest(LocalDate.of(2026, 10, 1), category.getId(),
                new BigDecimal(amount), description, PaymentMethod.UPI);
    }

    private void createOn(Category category, String date) {
        expenseService.create(new ExpenseRequest(LocalDate.parse(date), category.getId(),
                new BigDecimal("10.00"), "x", PaymentMethod.UPI));
    }

    @Test
    void createReturnsSavedExpenseWithGeneratedIdAndCategoryName() {
        Category category = newCategory();

        ExpenseResponse response = expenseService.create(requestFor(category, "120.00", "Lunch"));

        assertNotNull(response.id());
        assertEquals("Service Test Category", response.categoryName());
        assertEquals(0, new BigDecimal("120.00").compareTo(response.amount()));
        assertNotNull(response.createdAt());
    }

    @Test
    void createWithUnknownCategoryThrowsNotFound() {
        ExpenseRequest request = new ExpenseRequest(LocalDate.of(2026, 10, 1), 999_999L,
                new BigDecimal("10.00"), "Ghost category", PaymentMethod.CASH);

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
        entityManager.flush();
        entityManager.clear();

        assertThrows(ResourceNotFoundException.class, () -> expenseService.getById(id));
    }

    @Test
    void getAllWithDateRangeIncludesBothEndsAndExcludesOutside() {
        Category category = newCategory();
        createOn(category, "2026-09-30");
        createOn(category, "2026-10-01");
        createOn(category, "2026-10-31");
        createOn(category, "2026-11-01");

        List<ExpenseResponse> october =
                expenseService.getAll(new ExpenseFilter(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), null, null));

        assertEquals(2, october.size());
        assertEquals(LocalDate.of(2026, 10, 31), october.get(0).expenseDate());
    }

    @Test
    void getAllWithOnlyOneDateOrReversedRangeThrowsBadRequest() {
        assertThrows(BadRequestException.class,
                () -> expenseService.getAll(new ExpenseFilter(LocalDate.of(2026, 10, 1), null, null, null)));
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

    @Test
    void listingExpensesLoadsCategoriesInOneQuery() {
        Category first = categoryRepository.save(new Category("N Plus One Test A"));
        Category second = categoryRepository.save(new Category("N Plus One Test B"));
        Category third = categoryRepository.save(new Category("N Plus One Test C"));

        createOn(first, "2026-10-01");
        createOn(second, "2026-10-02");
        createOn(third, "2026-10-03");

        entityManager.flush();
        entityManager.clear();

        Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        List<ExpenseResponse> result =
                expenseService.getAll(new ExpenseFilter(null, null, null, null));

        assertEquals(3, result.size());
        assertEquals(1, stats.getPrepareStatementCount());
    }
}