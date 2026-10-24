import { Link } from "react-router";
import type { Expense } from "../types/expense";
import { formatCurrency } from "../utils/formatCurrency";
import { formatDisplayDate } from "../utils/dateUtils";

interface ExpenseRowProps {
  expense: Expense;
  paymentLabel: string;
  onDelete: (expense: Expense) => void;
  isDeleting: boolean; 
}

// Presentational component: receives data through props, fetches nothing.
export default function ExpenseRow({ expense, paymentLabel, onDelete, isDeleting }: ExpenseRowProps) {
  // Fall back to the category name when no description was entered.
  const title = expense.description?.trim() ? expense.description : expense.categoryName;

  return (
     <li className="px-3 py-3">
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <p className="truncate font-medium">{title}</p>
          <p className="text-sm text-gray-500">
            {formatDisplayDate(expense.expenseDate)} · {expense.categoryName} · {paymentLabel}
          </p>
        </div>
        <p className="shrink-0 font-semibold">{formatCurrency(expense.amount)}</p>
      </div>

      {/* py-2 pr-2 gives each action a bigger tap area on a phone */}
      <div className="mt-1 flex gap-4 text-sm">
        {/* Link navigates without a page reload; "expense.id" fills the :id part of the route */}
        <Link to={`/expenses/${expense.id}/edit`} className="py-2 pr-2 font-medium text-blue-600">
          Edit
        </Link>
        <button
          type="button"
          onClick={() => onDelete(expense)}
          disabled={isDeleting}
          className="py-2 pr-2 font-medium text-red-600 disabled:opacity-50"
        >
          {isDeleting ? "Deleting…" : "Delete"}
        </button>
      </div>
    </li>
  );
}