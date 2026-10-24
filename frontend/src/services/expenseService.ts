import { apiDelete, apiGet, apiPost, apiPut } from "./apiClient";
import type { Expense, ExpenseFilters, ExpenseInput } from "../types/expense";

export function getExpenses(filters: ExpenseFilters = {}): Promise<Expense[]> {
    const params = new URLSearchParams();

    for (const [key, value] of Object.entries(filters)) {
        if (value !== undefined && value !== "") {
            params.set(key, String(value));
        }
    }

    const query = params.toString();
    return apiGet<Expense[]>(`/api/expenses${query ? `?${query}` : ""}`);
}

export function createExpense(input: ExpenseInput): Promise<Expense> {
    return apiPost<Expense>("/api/expenses", input);
}

export function getExpenseById(id: number): Promise<Expense> {
    return apiGet<Expense>(`/api/expenses/${id}`);
}

export function updateExpense(id: number, input: ExpenseInput): Promise<Expense> {
    return apiPut<Expense>(`/api/expenses/${id}`, input);
}

export function deleteExpense(id: number): Promise<void> {
    return apiDelete(`/api/expenses/${id}`);
}