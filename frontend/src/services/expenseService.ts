import { apiGet } from "./apiClient";
import type { Expense, ExpenseFilters } from "../types/expense";

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