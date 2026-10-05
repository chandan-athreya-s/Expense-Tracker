package com.example.expensetracker.service;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseResponse;
import com.example.expensetracker.entity.Category;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.exception.ResourceNotFoundException;
import com.example.expensetracker.repository.CategoryRepository;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * @Service marks this class as a Spring-managed business-logic component.
 *
 * @Transactional(readOnly = true) at class level means: every method runs inside a
 * database transaction that is read-only by default (a hint that lets the database
 * and Hibernate skip unnecessary work). Methods that change data override it below
 * with a plain @Transactional.
 */
@Service
@Transactional(readOnly = true)
public class ExpenseService {

    // The chef's two helpers. final + constructor injection: Spring supplies them
    // when it creates the service, and they can never be null or swapped later.
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional // read-write: this method changes data
    public ExpenseResponse create(ExpenseRequest request) {
        Category category = findCategory(request.categoryId());

        Expense expense = new Expense();
        applyRequest(expense, request, category);

        // saveAndFlush = save + send the SQL to the database right now, so the
        // auto-generated id and timestamps are filled in before we build the response.
        return ExpenseResponse.from(expenseRepository.saveAndFlush(expense));
    }

    public ExpenseResponse getById(Long id) {
        return ExpenseResponse.from(findExpense(id));
    }

    // Newest first. For now this returns everything; month/category filtering
    // arrives in Milestone 4.
    public List<ExpenseResponse> getAll() {
        Sort newestFirst = Sort.by(Sort.Order.desc("expenseDate"), Sort.Order.desc("id"));
        return expenseRepository.findAll(newestFirst).stream()
                .map(ExpenseResponse::from)
                .toList();
    }

    @Transactional
    public ExpenseResponse update(Long id, ExpenseRequest request) {
        Expense expense = findExpense(id);
        Category category = findCategory(request.categoryId());

        // 'expense' is a managed entity: Hibernate tracks it. Changing its fields is
        // enough, and Hibernate writes an UPDATE on flush (called "dirty checking").
        applyRequest(expense, request, category);
        return ExpenseResponse.from(expenseRepository.saveAndFlush(expense));
    }

    @Transactional
    public void delete(Long id) {
        // Check first so a missing id gives a clear "not found" instead of silently doing nothing.
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense not found with id " + id);
        }
        expenseRepository.deleteById(id);
    }

    // ---- private helpers ----

    private Expense findExpense(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id " + id));
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
    }

    // Shared by create and update, so the field-copying logic lives in one place.
    // Note we never copy id/createdAt/updatedAt: the database layer owns those.
    private void applyRequest(Expense expense, ExpenseRequest request, Category category) {
        expense.setExpenseDate(request.date());
        expense.setCategory(category);
        expense.setAmount(request.amount());
        expense.setDescription(request.description());
        expense.setPaymentMethod(request.paymentMethod());    }
}