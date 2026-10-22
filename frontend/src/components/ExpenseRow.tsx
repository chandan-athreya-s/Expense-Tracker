import type { Expense } from "../types/expense";
import { formatCurrency } from "../utils/formatCurrency";
import { formatDisplayDate } from "../utils/dateUtils";

interface ExpenseRowProps {
  expense: Expense;
  paymentLabel: string; 
}

// Presentational component: receives data through props, fetches nothing.
export default function ExpenseRow({ expense, paymentLabel }: ExpenseRowProps) {
  // Fall back to the category name when no description was entered.
  const title = expense.description?.trim() ? expense.description : expense.categoryName;

  return (
    <li className="flex items-start justify-between gap-3 px-3 py-3">
      <div className="min-w-0">
        <p className="truncate font-medium">{title}</p>
        <p className="text-sm text-gray-500">
          {formatDisplayDate(expense.expenseDate)} · {expense.categoryName} · {paymentLabel}
        </p>
      </div>
      <p className="shrink-0 font-semibold">{formatCurrency(expense.amount)}</p>
    </li>
  );
}