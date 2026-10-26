import { apiGet, apiPut } from "./apiClient";
import type { BudgetStatus } from "../types/budget";

export function getBudgetStatus(year: number, month: number): Promise<BudgetStatus> {
    return apiGet<BudgetStatus>(`/api/budgets/${year}/${month}/status`);
}

export function setBudget(year: number, month:number, amount: number): Promise<unknown> {
    return apiPut<unknown>(`/api/budgets/${year}/${month}`, { amount });
}