export interface CategoryTotal {
    categoryId: number;
    categoryName: string;
    totalSpent: number;
}

export interface DailyTotal {
    expenseDate: string;
    totalSpent: number;
}