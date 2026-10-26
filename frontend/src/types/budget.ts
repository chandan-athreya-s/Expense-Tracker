export interface BudgetStatus{
    year: number;
    month: number;
    budgetSet: boolean;
    budget: number | null;
    totalSpent: number;
    remaining: number | null;
    percentUsed: number | null;
    exceeded: boolean;
}