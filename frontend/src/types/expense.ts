export interface Expense {
    id: number;
    expenseDate: string;
    categoryId: number;
    categoryName: string;
    amount: number;
    description: string | null;
    paymentMethod: string;
    createdAt: string;
    updatedAt: string;
}

export interface ExpenseFilters {
    startDate?: string;
    endDate?: string;
    categoryId?: number;
    paymentMethod?: string;
}